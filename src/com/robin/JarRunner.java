package com.robin;

import java.io.File;
import java.util.ArrayList;

import com.robin.tail.LogPosiUtil;
import com.robin.util.ConfInfo;

/**
 * The main entrance of the jar 
 * 
 * It starts either  a ClientServer which is used to read multiple log file and send
 * them to server, or a Server which is used to receive log files and write them to
 * the server machine.
 * 
 * @param args
 */

public class JarRunner {
		public static void main(String[] args) {
		if (args == null || args.length < 1) {
			System.out
					.println("Usage: java -jar thisjar.jar server/client -f properties.conf");
			return;
		}
		System.out.println(args.length);
		String url = args[1];
		System.out.println(url);
		File f = new File(url);
		if (!f.exists()) {
			System.out.println("Property file does't exsit!");
			return;
		}
		ConfInfo conf = ConfInfo.getInstance(url);
		if (args[0].equals("server")) {
			LogPosiUtil.timelySave(conf);
			System.out.println("Starting Server.......");
			startServer(conf.getMqIp(), conf.getOutLogFileURL());
		}
		if (args[0].equals("client")) {
			System.out.println("Starting Client.......");
			startClient(conf.getMqIp(), conf.getInLogFileURLList(),
					conf.getClientFreshInterval());
		}
	}

	private static void startServer(String ip, String url) {
		ServerStarter ss = new ServerStarter();
		ss.startRCPServer(ip);
		ss.startServerWriter(ip, url);
	}

	private static void startClient(String ip, ArrayList<String> urllist,
			long interval) {
		ClientStarter test = new ClientStarter(ip);
		for (String url : urllist)
			test.startAFile(url, interval);
	}

}
