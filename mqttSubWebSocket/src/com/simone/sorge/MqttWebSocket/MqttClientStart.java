package com.simone.sorge.MqttWebSocket;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.Token;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttClientStart implements MqttCallback
{
	
	IMqttAsyncClient myClient;
	//MqttClient myClient;
	MqttConnectOptions connOpt;
	
	static final String broker = "tcp://localhost:1883";
	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("Connection Lost");
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		System.out.println("----------------------");
		System.out.println("|Topic: "+topic);
		System.out.println("|Message: "+new String(message.getPayload()));
		System.out.println("-----------------------");
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String args[])
	{
		MqttClientStart client = new MqttClientStart();
		client.runClient();
	}
	public void runClient()
	{
		String clientID = "WebSocketExample";
		IMqttToken connectToken = null;
		 IMqttToken subToken = null;
		 IMqttToken disconnectToken = null;
		 
		connOpt = new MqttConnectOptions();
		connOpt.setCleanSession(true);
		connOpt.setKeepAliveInterval(30);
		try
		{
			myClient = new MqttWebSocketAsyncClient(broker,clientID);
			//myClient = new MqttClient(broker, clientID);
			myClient.setCallback(this);
			connectToken = myClient.connect(connOpt,null,null);
			connectToken.waitForCompletion();
			
			
		} catch(MqttException e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("connect to: "+broker);
		String myTopic = "pahodemo/test";
		try {
			int subQoS = 0;
			subToken = myClient.subscribe(myTopic, subQoS,null,null);
			subToken.waitForCompletion();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// wait to ensure subscribed messages are delivered
				Thread.sleep(50000);
			disconnectToken=myClient.disconnect();
			disconnectToken.waitForCompletion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
