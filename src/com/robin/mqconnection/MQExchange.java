package com.robin.mqconnection;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * In rabbitMQ, several concept should be decleared.
 * 
 * @param Connection
 *            : a connection is bind to a Ip, which means a host only should
 *            have one connection.
 * 
 * @param Chanel
 *            : channel is 1v1/1vn to exchange. but one channel can have many
 *            queues.
 * 
 * @param Exchange
 *            :
 * 
 * @param Queue
 *            : a queue is related to 2 things. the routing rules and the
 *            channel. A channel can bind many queues, and each queue can have
 *            many tags by the following sentences. channel.queueBind(queueName,
 *            receiver.exChangeName, tag);
 * 
 * @show: How a message transffer in the system. A message is sent with a
 *        routing key and a exchange name. The routing key determing which
 *        queues it should arrive.
 * 
 * @param Consumer
 *            ; is binded to a queue.
 * 
 * @author you.meng
 * 
 */
public class MQExchange {

	ConnectionFactory factory;
	Connection connection;
	Channel channel;

	static int receiverNum = 0;

	/**
	 * 
	 * @param connetionIp
	 * @throws IOException
	 */
	public void buildConnenction(String exchangeName, String connetionIp,
			boolean useTopic) throws IOException {
		factory = new ConnectionFactory();
		factory.setHost("localhost");
		connection = factory.newConnection();
		channel = connection.createChannel();
		// channel is binded to a exchangeName
		if (useTopic)
			channel.exchangeDeclare(exchangeName, "topic");
		else
			channel.exchangeDeclare(exchangeName, "direct");
	}

	public void sendInfo(String exchangeName, String routingkey, String info)
			throws IOException {
		System.out.println("Sending infos...." + routingkey + " " + info);
		// send info only related to a channel and it's exchangeName and
		// routking key, it is not related to a queue
		channel.basicPublish(exchangeName, routingkey, null, info.getBytes());
	}

	public void registReceiver(final BaseReceiver receiver)
			throws IOException, ShutdownSignalException,
			ConsumerCancelledException, InterruptedException {
		receiverNum++;
		Thread receiverThread = new Thread() {
			@Override
			public void run() {
				String queueName;
				try {
					queueName = channel.queueDeclare().getQueue();
					for (String tag : receiver.tags) {
						channel.queueBind(queueName, receiver.exChangeName, tag);
					}
					QueueingConsumer consumer = new QueueingConsumer(channel);
					channel.basicConsume(queueName, true, consumer);
					
					System.out
							.println(" [*] Start waiting for messages............ current ReceiverNum:"
									+ receiverNum);
					while (true) {
						QueueingConsumer.Delivery delivery = consumer
								.nextDelivery();
						String message2 = new String(delivery.getBody());
						String routingKey = delivery.getEnvelope()
								.getRoutingKey();
						receiver.onMessageArrive(message2, routingKey);
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (RuntimeException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 

			}
		};
		receiverThread.start();
	}

	public void closeConnection() {
		try {
			channel.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
