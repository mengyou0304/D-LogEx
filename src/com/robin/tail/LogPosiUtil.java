package com.robin.tail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.robin.util.ConfInfo;
import com.robin.util.LogEXFileUtility;

/**
 * used for where we have of the log
 * 
 * @author you.meng
 * 
 */
public class LogPosiUtil {
	ConfInfo conf;

	private LogPosiUtil() {
	}

	// fileCurrentLength Map
	static HashMap<String, Long> fileCLMap = new HashMap<String, Long>();

	public static void setFileCSLen(String hostid, String fileid, Long length) {
		String logUID = getUID(hostid, fileid);
		fileCLMap.put(logUID, length);
	}

	public static void clearInfo() {
		fileCLMap.clear();
	}

	public static Long getFileCSLen(String hostid, String url) {
		String logUID = getUID(hostid, url);
		Long length = fileCLMap.get(logUID);
		if (length == null)
			return 0l;
		return length;
	}

	public static String getRouterKey(String ip, String fileUrl, long length) {
		return "log." + ip + "!" + fileUrl + "!" + length;
	}

	public static void decodeRouterKey(String routerKey) {
		String[] ss = routerKey.split("!");
		if (ss.length != 3)
			throw new RuntimeException(
					"Router key decode error! Suppose to have two '!' Don't contain any '!' in file path "
							+ routerKey);
		setFileCSLen(ss[0], ss[1], Long.parseLong(ss[2]));
	}

	private static String getUID(String hostid, String fileid) {
		return hostid + "_" + fileid;
	}

	public static void timelySave(final ConfInfo conf) {
		loadToMem(conf.getPostionFileURL());
		Thread saveThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(1000);
						System.out.println("Writing current status into file:"
								+ conf.getPostionFileURL());
						saveToFile(conf.getPostionFileURL());
						Thread.sleep(1000 * 150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		saveThread.start();
	}

	private static void saveToFile(String url) {
		Iterator<String> it = fileCLMap.keySet().iterator();
		String s = "";
		while (it.hasNext()) {
			String key = it.next();
			Long value = fileCLMap.get(key);
			String line = key + ":" + value;
			s += line + "\n";
		}
		try {
			LogEXFileUtility.removeAndWrite(url, s);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void loadToMem(String url) {
		System.out.println("Loading current status into file:" + url);
		String context = LogEXFileUtility.readAll(url);
		String[] ss = context.split("\n");
		fileCLMap = new HashMap<String, Long>();
		for (String s : ss) {
			String[] ts = s.split(":");
			if (ts.length < 2)
				continue;
			fileCLMap.put(ts[0], Long.parseLong(ts[1]));
		}
	}

	private static void show() {
		System.out.println("\n\nShow memSaved.........");
		Iterator<String> it = fileCLMap.keySet().iterator();
		String s = "";
		while (it.hasNext()) {
			String key = it.next();
			Long value = fileCLMap.get(key);
			String line = key + ":" + value;
			s += line + "\n";
		}
		System.out.println(s);
	}

	public static void main(String[] args) {
		LogPosiUtil.setFileCSLen("127.0.0.1", "/a/b/c/d", 200l);
		LogPosiUtil.setFileCSLen("127.0.2.1", "/a/b/232/d", 201l);
		LogPosiUtil.setFileCSLen("127.0.4.1", "/a/b/c1231/d", 220l);
		String ppfileURL = "/Users/you.meng/tmp2/logpp2";
		LogPosiUtil.saveToFile(ppfileURL);
		LogPosiUtil.clearInfo();
		LogPosiUtil.loadToMem(ppfileURL);
		LogPosiUtil.show();
	}
}
