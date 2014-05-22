package com.robin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.robin.mqconnection.Configuration;
import com.robin.mqconnection.MQExchange;
import com.robin.mqconnection.rpc.ParamParser;
import com.robin.mqconnection.rpc.RCPClientListener;
import com.robin.mqconnection.rpc.RPCClient;
import com.robin.tail.LogFileTailer;
import com.robin.tail.LogFileTailerListener;
import com.robin.tail.LogPosiUtil;
import com.robin.util.ConfInfo;

/**
 * It is used to start a Client. It includes: MQExchange: for log message
 * transffer RPCClient: for async RPCCall both the exchange and the rpc is use
 * Rabbitmq for message transffer It also start a file tailer to perform action
 * like "tail -f" for a log file.
 * 
 * @defact it only contain only one tailer for each client
 * @author you.meng
 * 
 */
public class ClientStarter {
	RPCClient rpcClient;
	MQExchange mqExchange;
	String routerParaSep = "!";
	String MQip = null;
	HashMap<String, LogFileTailer> tailerMap = new HashMap<String, LogFileTailer>();

	public ClientStarter(String ip) {
		MQip = ip;
		mqExchange = new MQExchange();
		try {
			mqExchange.buildConnenction(Configuration.ExchangeName, ip, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startFileTailer(String url, long sampleInterval,
			long startLength, LogFileTailerListener logFileListener,
			boolean async) {
		LogFileTailer lft = new LogFileTailer(new File(url), sampleInterval,
				startLength);
		lft.addLogFileTailerListener(logFileListener);
		tailerMap.put(url, lft);
		if (async)
			lft.start();
		else
			lft.startTailSameThread();
	}

	public void initRCPConnnection(RCPClientListener rcpListener) {
		ArrayList<String> paralist = new ArrayList<String>();
		paralist.add("ip");
		paralist.add("url");
		ParamParser p = new ParamParser(paralist);
		rpcClient = new RPCClient(rcpListener, "rpc_queue", "localhost", p);
	}

	public void performCall(HashMap<String, String> paraMap)
			throws IOException, RuntimeException, InterruptedException {
		String r1 = rpcClient.call(paraMap);
		System.out.println("Get response from Server: " + r1);
		rpcClient.close();
	}

	private void sendInfo(String exchangeName, String router, String line) {
		try {
			mqExchange.sendInfo(exchangeName, router, line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * The method to start a client process. In fact whatever how many client it
	 * started, they only start sevral things:
	 * 
	 * RCPClientListener: LogFileTailerListener: RCPClient: this client is
	 * comparable light, because it only start once,and won't start any more
	 * during the tail
	 * 
	 * 
	 * And they share the following things: MQExchange: The heavy message
	 * transffer component which will hold connection all throught the whole
	 * tailing process
	 * 
	 * 
	 * 
	 * @param url
	 * @param sampleInterval
	 */
	public void startAFile(final String url, final Long sampleInterval) {
		// This Listener will be called after Server return the lineNum of url
		// file
		RCPClientListener RcpListener = new RCPClientListener() {
			@Override
			public void onCallBack(String value) {
				// This is Step2: after server returns the position, it starts a
				// tailer to monitor
				// the log file
				final LogFileTailerListener listener = new LogFileTailerListener() {
					@Override
					public void newLogFileLine(String line, long length) {
						// This is step3: Once the log file changes, it send
						// messages to server
						sendInfo(
								Configuration.ExchangeName,
								LogPosiUtil.getRouterKey(
										MQip, url, length), line);
					}
				};
				startFileTailer(url, sampleInterval, Integer.parseInt(value),
						listener, true);
			}
		};

		// These lines performe a RPC call to get the lineNum of the url file
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("ip", MQip);
		map.put("url", url);
		try {
			initRCPConnnection(RcpListener);
			// This is Step1: RCP Call to get a log file's current position.
			performCall(map);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		ConfInfo conf = ConfInfo.getInstance("/Users/you.meng/mysoft/p.conf");
		System.out.println("Starting Client.......");
		ClientStarter test = new ClientStarter(conf.getMqIp());
		for (String url : conf.getInLogFileURLList())
			test.startAFile(url, conf.getClientFreshInterval());
	}
}
