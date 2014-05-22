package com.robin.exclassification.result;

import com.robin.util.LogEXFileUtility;

public class ResultFilePusher {
	private static ResultFilePusher instance;
	
	private ResultFilePusher(){
		
	}
	public synchronized static ResultFilePusher getInstance(){
		if(instance==null)
			instance=new ResultFilePusher();
		return instance;
	}
	
	public void writeToFile(String line){
		LogEXFileUtility fileUtil=new LogEXFileUtility("/Users/you.meng/tmp/classifiedEx.db");
		fileUtil.startWrite();
		fileUtil.write(line);
		fileUtil.closeWrite();
	}

}
