package com.robin.mqconnection;

import java.util.ArrayList;

import com.robin.util.LogEXFileUtility;

public abstract class BaseReceiver {
	public String exChangeName;
	public ArrayList<String> tags;
	LogEXFileUtility writer=null;

	public BaseReceiver(String exChangeName, ArrayList<String> tags,String url) {
		this.exChangeName = exChangeName;
		this.tags = tags;
		writer=new LogEXFileUtility(url);
		writer.startWrite();
	}
	public void fileWrite(String line){
		writer.write(line);
	}
	public abstract void onMessageArrive(String message, String routingKey);

}
