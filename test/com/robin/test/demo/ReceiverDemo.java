package com.robin.test.demo;

import java.io.IOException;
import java.util.ArrayList;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import com.robin.mqconnection.BaseReceiver;
import com.robin.mqconnection.Configuration;
import com.robin.mqconnection.MQExchange;

public class ReceiverDemo {
	public static void main(String[] args) {
		ArrayList<String> taglist1 = new ArrayList<String>();
		// pattern: name.age.school
		taglist1.add("rcp.#");
		BaseReceiver br = new BaseReceiver(Configuration.ExchangeName, taglist1, "/Users/you.meng/tmp/rcp.log") {
			long lineNums=0;
			@Override
			public void onMessageArrive(String message, String routingKey) {
				if(lineNums%1000==0)
					System.out.println(lineNums);
				lineNums++;
				fileWrite("["+lineNums+"] ["+routingKey+"] "+message+"\n");
				System.out.println("1 has a message "+message);
			}
		};
		ArrayList<String> taglist2 = new ArrayList<String>();
		// pattern: name.age.school
		taglist2.add("#.mengyou");
		BaseReceiver br2 = new BaseReceiver(Configuration.ExchangeName, taglist2, "/Users/you.meng/tmp/logs.log") {
			long lineNums=0;
			@Override
			public void onMessageArrive(String message, String routingKey) {
				if(lineNums%1000==0)
					System.out.println(lineNums);
				lineNums++;
				fileWrite("["+lineNums+"] ["+routingKey+"] "+message+"\n");
				System.out.println("2 has a message "+message);
			}
		};
		
		ArrayList<String> taglist3 = new ArrayList<String>();
		// pattern: name.age.school
		taglist3.add("log.#");
		BaseReceiver br3 = new BaseReceiver(Configuration.ExchangeName, taglist3, "/Users/you.meng/tmp/exception.log") {
			long lineNums=0;
			@Override
			public void onMessageArrive(String message, String routingKey) {
				if(lineNums%1000==0)
					System.out.println(lineNums);
				lineNums++;
				fileWrite("["+lineNums+"] ["+routingKey+"] "+message+"\n");
				System.out.println("3 has a message "+message);
			}
		};

		try {
			MQExchange mec = new MQExchange();
			mec.buildConnenction(Configuration.ExchangeName, "localhost",true);
			mec.registReceiver(br3);
			mec.registReceiver(br2);
			mec.registReceiver(br);
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
	

}
