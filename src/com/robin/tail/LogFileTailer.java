package com.robin.tail;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashSet;
import java.util.Iterator;

public class LogFileTailer extends Thread {

	private long sampleTimeInterval = 9000;
	
	private long filePointer=0;
	
	private File logfile;

	private boolean tailing = true;

	private HashSet<LogFileTailerListener> listeners = new HashSet<LogFileTailerListener>();

	public LogFileTailer(File file) {
		this.logfile = file;
	}

	public LogFileTailer(File file, long sampleInterval,
			long startLength) {
		this.logfile = file;
		this.sampleTimeInterval = sampleInterval;
		this.filePointer=startLength;
	}

	public void addLogFileTailerListener(LogFileTailerListener l) {
		this.listeners.add(l);
	}

	public void removeLogFileTailerListener(LogFileTailerListener l) {
		this.listeners.remove(l);
	}

	protected void fireNewLogFileLine(String line,long length) {
		for (Iterator<LogFileTailerListener> i = this.listeners.iterator(); i.hasNext();) {
			LogFileTailerListener l =  i.next();
			l.newLogFileLine(line,length);
		}
	}

	public void stopTailing() {
		this.tailing = false;
	}
	public void startTailSameThread(){
		RandomAccessFile rfile=null;
		if(!logfile.exists())
			try {
				logfile.getParentFile().mkdirs();
				logfile.createNewFile();
				System.out.println("Creating tail file as: "+logfile.getAbsolutePath());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		try {
			while (this.tailing) {
				rfile = new RandomAccessFile(logfile, "r");
				long fileLength = this.logfile.length();
				if (fileLength < filePointer) {
					throw new RuntimeException("setted LogfileLength exceed the orignial log length. Please check the log file length again :"+logfile.getAbsolutePath());
				}
				if (fileLength > filePointer) {
					rfile.seek(filePointer);
					String line = rfile.readLine();
					while (line != null) {
						this.fireNewLogFileLine(line,rfile.getFilePointer());
						line = rfile.readLine();
					}
					filePointer = rfile.getFilePointer();
				}
				sleep(this.sampleTimeInterval);
				rfile.close();
				System.out.println("Scanning the log files......"+logfile.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				rfile.close();
			} catch (IOException e) {
				
			}
		}

	}
	public void run() {
		startTailSameThread();
	}

	public void startTailing() {
		this.tailing = true;
	}

	public long getSampleTimeInterval() {
		return sampleTimeInterval;
	}

	public void setSampleTimeInterval(long sampleTimeInterval) {
		this.sampleTimeInterval = sampleTimeInterval;
	}
	

}
