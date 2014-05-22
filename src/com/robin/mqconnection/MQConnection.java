package com.robin.mqconnection;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
/**
 * used for transfer info to all the queues that registed
 * @author you.meng
 *
 */
@Deprecated
public class MQConnection {
	ConnectionFactory factory;
	Connection connection;
	Channel channel;
	
	public void sendInfo(String queueName,String info) throws IOException{
		channel.queueDeclare(queueName, false, false, false, null);
		String message = "Hello World!";
		channel.basicPublish("", queueName, null, message.getBytes());
	}
	public void bindingReceiver(String queueName) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException{
		channel.queueDeclare(queueName, false, false, false, null);
		System.out.println(" [*] Waiting for messages............");
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume(queueName, true, consumer);
		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message2 = new String(delivery.getBody());
			System.out.println(" [x] Received '" + message2 + "'");
		}
	}
	public void buildConnenction() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();
	}

	public static void main(String[] args) {
		try {
			MQConnection mec = new MQConnection();
			mec.buildConnenction();
			mec.bindingReceiver("Q1");
			mec.bindingReceiver("Q2");
			mec.sendInfo("Q1","Hello Q1!");
			mec.sendInfo("Q1","Hello Q1!");
			mec.sendInfo("Q2","Hello Q2!");
			mec.sendInfo("Q2","Hello Q2!");
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
