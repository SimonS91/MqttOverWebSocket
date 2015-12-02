package com.simone.sorge.mqttPublish;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttPublish 
{
	IMqttAsyncClient myClient;
	static final String broker = "tcp://localhost:1883";
	static final String id = "publisher";
	
	public MqttPublish()
	{
		
	}
	
	public static void main(String args[])
	{
		new MqttPublish().runClient();
	}
	
	public void runClient()
	{
		IMqttToken connectToken = null;
		IMqttToken pubToken = null;
	    IMqttToken disconnectToken = null;
		try
		{
			myClient = new MqttWebSocketAsyncClient(broker,id);
			connectToken = myClient.connect(null,null);
			connectToken.waitForCompletion();
			MqttMessage message = new MqttMessage();
			message.setPayload("A single message".getBytes());
			System.out.println("messaggio inviato");
			pubToken = myClient.publish("pahodemo/test", message,null,null);
			pubToken.waitForCompletion();
			disconnectToken = myClient.disconnect();
			disconnectToken.waitForCompletion();
		} catch(MqttException e)
		{
			e.printStackTrace();
		}
	}
}
