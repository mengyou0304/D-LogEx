package com.robin.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Read the config file to get these infos
 * 
 * @MQ_IP: All needed, 
 * @FRESH_TIME: Client only, 
 * @IN_URLS: Client only, 
 * @OUT_URL:Server only,
 * @POSITION_FILE_URL: Server only,
 * 
 * @author you.meng
 * 
 */
public class ConfInfo {

	private ArrayList<String> inLogFileURLList = new ArrayList<String>();
	private String outLogFileURL = "";
	private String postionFileURL = "";
	private String mqIp = "";
	private Long clientFreshInterval;
	private String OUT_EX_Logstash;

	private static ConfInfo instance;

	public static ConfInfo getInstance(String url) {
		if (instance != null)
			return instance;
		try {
			if(url==null||url.length()==0)
				throw new RuntimeException("No Path for the configure file is set");
			instance = real_getConfigs(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return instance;
	}

	private ConfInfo() {

	}

	public ArrayList<String> getInLogFileURLList() {
		return inLogFileURLList;
	}

	public String getOutLogFileURL() {
		return outLogFileURL;
	}

	public long getClientFreshInterval() {
		return clientFreshInterval;
	}

	public String getMqIp() {
		return mqIp;
	}

	public String getPostionFileURL() {
		return postionFileURL;
	}
	

	public String getOUT_EX_Logstash() {
		return OUT_EX_Logstash;
	}

	public void setOUT_EX_Logstash(String oUT_EX_Logstash) {
		OUT_EX_Logstash = oUT_EX_Logstash;
	}

	private static ConfInfo real_getConfigs(String URL) throws IOException {
		InputStream in = new BufferedInputStream(new FileInputStream(URL));
		Properties p = new Properties();
		p.load(in);
		ConfInfo conInfo = new ConfInfo();
		conInfo.mqIp = p.getProperty("MQ_IP", "localhost");
		conInfo.outLogFileURL = p.getProperty("OUT_URL", "/tmp/log/log1.log");
		conInfo.postionFileURL = p.getProperty("POSITION_FILE_URL",
				"/tmp/log/logp.db");
		conInfo.clientFreshInterval = Long.parseLong(p.getProperty(
				"FRESH_TIME", "7000"));
		conInfo.OUT_EX_Logstash=p.getProperty("OUT_EX_Logstash");
		ArrayList<String> logList = conInfo.getInLogFileURLList();
		if (logList == null)
			logList = new ArrayList<String>();
		String infiles = p.getProperty("IN_URLS", "");
		String[] ss = infiles.split(";");
		for (String s : ss) {
			s = s.trim();
			if (s.length() < 1)
				continue;
			logList.add(s);
		}
		return conInfo;
	}
}
