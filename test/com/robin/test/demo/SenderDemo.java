package com.robin.test.demo;

import java.io.IOException;
import java.util.ArrayList;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.robin.mqconnection.Configuration;
import com.robin.mqconnection.MQExchange;

public class SenderDemo {
	public static void main(String[] args) {
		try {
			MQExchange mec = new MQExchange();
			mec.buildConnenction(Configuration.ExchangeName, "localhost",true);
			mec.sendInfo(Configuration.ExchangeName,"log.hello.mengyou","This is message of mengyou's");
			mec.sendInfo(Configuration.ExchangeName,"rcp.jinrui.28.tsinghua","This is message from jinrui");
			mec.sendInfo(Configuration.ExchangeName,"rcp.jiamengqi.25.bupt","This is message from jiamengqi");
			mec.sendInfo(Configuration.ExchangeName,"exception.billy.28.somewhere","This is message from billy");
			mec.closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ShutdownSignalException e) {
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			e.printStackTrace();
		}
	}

}
