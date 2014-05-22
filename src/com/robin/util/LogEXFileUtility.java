package com.robin.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.robin.exclassification.listener.impl.LogstashPersistenceListener;

public class LogEXFileUtility {
	private static Logger logger = Logger.getLogger(LogEXFileUtility.class);  

	int conter = 0;
	int maxWriteLine = 20000;
	BufferedWriter br = null;
	File file = null;

	public static void removeAndWrite(String url, String s) throws IOException {
		FileWriter writer = new FileWriter(url);
		BufferedWriter out=new BufferedWriter(writer);
		out.write(s);
		out.flush();
		out.close();
		writer.close();
	}

	public static String readAll(String url) {
		String s = "";
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(url)));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				s += tempString + "\n";
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return s;
	}

	public LogEXFileUtility(String url) {
		try {
			file = new File(url);
			if (!file.exists()) {
				logger.info("Using file as " + url
						+ " don't exists, so creating new file");
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void startWrite() {
		try {
			br = new BufferedWriter(new FileWriter(file, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String line) {
		try {
			br.append(line);
			br.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeWrite() {
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void append(String content) throws IOException {

	}

	public static void main(String[] args) {
		LogEXFileUtility fr = new LogEXFileUtility("test12.file");
		Date last = new Date();
		try {
			for (int i = 0; i < 100000000; i++) {
				fr.append("This is a line of the file\n" + i);
				if (i % 100000 == 0) {
					Date d2 = new Date();
					System.out.println(d2.getTime() - last.getTime());
					last = d2;
				}
			}
			fr.closeWrite();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
