package com.robin.mqconnection.rpc;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;
import com.robin.tail.LogPosiUtil;

public class RPCServer extends Thread {
	public String RPC_QUEUE_NAME = "rpc_queue";
	RCPServerListener listener;
	Connection connection;
	Channel channel;
	QueueingConsumer consumer;

	ParamParser paraser;

	public void connect(String mqIP, ParamParser paraser) throws IOException,
			RuntimeException, InterruptedException {
		this.paraser = paraser;
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(mqIP);
		connection = factory.newConnection();
		channel = connection.createChannel();
				channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
		channel.basicQos(1);

		consumer = new QueueingConsumer(channel);
		channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
	}

	public void registListener(RCPServerListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		if (listener == null) {
			throw new RuntimeException("No RCPListener is set.");
		}
		System.out.println(" [x] Awaiting RPC requests");
		while (true) {
			QueueingConsumer.Delivery delivery=null;
			try {
				delivery = consumer.nextDelivery();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				break;
			}
			BasicProperties props = delivery.getProperties();

			BasicProperties replyProps = new BasicProperties.Builder()
					.correlationId(props.getCorrelationId()).build();

			String message = new String(delivery.getBody());
			System.out.println("Server [" + RPC_QUEUE_NAME
					+ "] receive call with param: (" + message + ")");
			String response = "" + listener.onCall(paraser.getParams(message));
			try {
				channel.basicPublish("", props.getReplyTo(), replyProps,
						response.getBytes());
				channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		ParamParser p = new ParamParser(null);
		RPCServer rpc = new RPCServer();
		try {
			rpc.connect("localhost", p);
			rpc.registListener(new RCPServerListener() {
				@Override
				public String onCall(HashMap<String, String> map) {
					Iterator<String> it = map.keySet().iterator();
					while (it.hasNext()) {
						String key = it.next();
						String value = map.get(key);
						System.out.println(key + ":" + value);
					}
					Long length = LogPosiUtil.getFileCSLen(map.get("ip"),
									map.get("url"));
					return String.valueOf(length);
				}
			});
			rpc.start();
		} catch (ShutdownSignalException e) {
			e.printStackTrace();
		} catch (ConsumerCancelledException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
