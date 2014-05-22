package com.robin.exclassification.listener.impl;

import org.apache.log4j.Logger;

import com.robin.exclassification.ExceptionEngine;
import com.robin.exclassification.ExceptionInfo;
import com.robin.exclassification.listener.OnExceptionListener;
import com.robin.util.LogEXFileUtility;

public class LogstashPersistenceListener extends OnExceptionListener{
	private static Logger logger = Logger.getLogger(LogstashPersistenceListener.class);  
    
	LogEXFileUtility writer;
	public LogstashPersistenceListener (String url){
		writer=new LogEXFileUtility(url);
		
	}
	@Override
	public void onExcetionArrive(ExceptionInfo info) {
		writer.startWrite();
		writer.write(info.getLogStashString()+"\n");
		writer.closeWrite();

	}
	public void close(){
	}

}
