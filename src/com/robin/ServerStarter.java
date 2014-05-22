package com.robin;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.robin.mqconnection.BaseReceiver;
import com.robin.mqconnection.Configuration;
import com.robin.mqconnection.MQExchange;
import com.robin.mqconnection.rpc.ParamParser;
import com.robin.mqconnection.rpc.RCPServerListener;
import com.robin.mqconnection.rpc.RPCServer;
import com.robin.tail.LogPosiUtil;
import com.robin.util.ConfInfo;

public class ServerStarter {
	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public void startRCPServer(String mqIP){
		try {
			ParamParser p=new ParamParser(null);
			RPCServer rpc = new RPCServer();
			rpc.connect(mqIP,p);
			rpc.registListener(new RCPServerListener() {
				@Override
				public String onCall(HashMap<String, String> map) {
					Long length=LogPosiUtil.getFileCSLen(map.get("ip"), map.get("url"));
					return String.valueOf(length);
				}
			});
			rpc.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void startServerWriter(String mqIP,String writeUrl){
		ArrayList<String> logTagList = new ArrayList<String>();
		logTagList.add("log.#");
		BaseReceiver br = new BaseReceiver(Configuration.ExchangeName, logTagList, writeUrl+".log") {
			long lineNums=0;
			@Override
			public void onMessageArrive(String message, String routingKey) {
				lineNums++;
				String line="["+lineNums+"] ["+routingKey+"] "+message+"\n";
				fileWrite(line);
				System.out.println("Log line is "+line);
				routingKey=routingKey.substring("log.".length());
				LogPosiUtil.decodeRouterKey(routingKey);
				if(lineNums%10==0)
					System.out.println("Arrive log num:"+lineNums+"\n one line as:"+line+"\n");
			
			}
		};
		ArrayList<String> exceptionList = new ArrayList<String>();
		exceptionList.add("exception.#");
		BaseReceiver br2 = new BaseReceiver(Configuration.ExchangeName, exceptionList, writeUrl+".exception") {
			long lineNums=0;
			@Override
			public void onMessageArrive(String message, String routingKey) {
				lineNums++;
				String line="["+lineNums+"] ["+routingKey+"] "+message+"\n";
				System.out.println("Exception line is "+line);
				fileWrite(line);
				if(lineNums%10==0)
					System.out.println("Arrive excetion num:"+lineNums+"\n one line as:"+line+"\n");
			}
		};
		try {
			MQExchange mec = new MQExchange();
			mec.buildConnenction(Configuration.ExchangeName, mqIP, true);
			mec.registReceiver(br);
			mec.registReceiver(br2);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		ServerStarter ss=new ServerStarter();
		ConfInfo conf = ConfInfo.getInstance("/Users/you.meng/mysoft/p.conf");
		LogPosiUtil.timelySave(conf);
		ss.startRCPServer(conf.getMqIp());
		ss.startServerWriter(conf.getMqIp(),conf.getOutLogFileURL());
		
	}
	

}
