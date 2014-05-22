package com.robin.mqconnection.rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

/**
 * A Basic RPCClient which can request RPCCall to the server.
 * 
 * @author you.meng
 * 
 */
public class RPCClient {
	private Connection connection;
	private Channel channel;
	public String requestQueueName = "rpc_queue";
	private String replyQueueName;
	private QueueingConsumer consumer;
	
	ParamParser paraParser; 
	String ip;
	HashMap<String,String> callingParams;
	RCPClientListener listener;

	public RPCClient(RCPClientListener listener,String methodName,String ip, ParamParser parser)  {
		this.listener=listener;
		this.requestQueueName=methodName;
		this.ip=ip;
		this.paraParser=parser;
	}

	public String call(HashMap<String,String> paraMap) throws IOException, RuntimeException, InterruptedException  {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(ip);
		connection = factory.newConnection();
		channel = connection.createChannel();
		replyQueueName = channel.queueDeclare().getQueue();
		consumer = new QueueingConsumer(channel);
		channel.basicConsume(replyQueueName, true, consumer);
	
		String response = null;
		//Using a corrID to Gurantee that the calls won't effect others
		String corrId = java.util.UUID.randomUUID().toString();
		BasicProperties props = new BasicProperties.Builder()
				.correlationId(corrId).replyTo(replyQueueName).build();
		String message=paraParser.getString(paraMap);
		channel.basicPublish("", requestQueueName, props, message.getBytes());
		//use this rotate to find the corresponding corrId which is exactly the result of the request
		while (true) {
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			if (delivery.getProperties().getCorrelationId().equals(corrId)) {
				response = new String(delivery.getBody());
				listener.onCallBack(response);
				break;
			}
		}
		return response;
	}

	public void close() throws IOException   {
		connection.close();
	}
	public static void main(String[] args) {
		ArrayList<String> paralist=new ArrayList<String>();
		paralist.add("ip");
		paralist.add("url");
		HashMap<String,String> map=new HashMap<String,String>();
		map.put("ip", "localhost");
		map.put("url", "/Users/you.meng/tmp/log3.log");
		ParamParser p=new ParamParser(paralist);
		RPCClient client2 = null;
		try {
			client2 = new RPCClient(new RCPClientListener() {
				@Override
				public void onCallBack(String value) {
					System.out.println("in client2: "+value);
				}
			},"rpc_queue", "localhost",p);
			Date d1 = new Date();
			for (int i = 0; i < 1; i++) {
				map.put("basic",String.valueOf(i + 1000));
				String r2 = client2.call(map);
				System.out.println("Get response from r2: " + r2);
			}
			Date d2 = new Date();
			System.out.println("Time used: " + (d2.getTime() - d1.getTime()));
			client2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
