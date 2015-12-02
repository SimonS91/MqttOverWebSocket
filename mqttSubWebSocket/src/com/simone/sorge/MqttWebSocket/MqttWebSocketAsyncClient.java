package com.simone.sorge.MqttWebSocket;

import java.net.URI;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.internal.NetworkModule;
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPingReq;
import org.eclipse.paho.client.mqttv3.logging.Logger;
import org.eclipse.paho.client.mqttv3.logging.LoggerFactory;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;

public class MqttWebSocketAsyncClient extends  MqttAsyncClient
{
	private static final String CLASS_NAME = MqttWebSocketAsyncClient.class.getName();
	private final Logger log;
	private final String serverURI;
	
	protected static String createDummyURI(String original)
	{
		if(!original.startsWith("ws:") && !original.startsWith("wss:"))
		{
			return original;
		}
		final URI uri = URI.create(original);
		return "tcp://DUMMY-"+uri.getHost()+ ":" + (uri.getPort() > 0 ? uri.getPort() : 80);
	}
	protected static boolean isDummyURI(String uri)
	{
		return uri.startsWith("tcp://DUMMY-");
	}
	public MqttWebSocketAsyncClient(String serverURI, String clientId,MqttClientPersistence persistence,String loggerName) throws MqttException 
	{
		super(createDummyURI(serverURI),clientId,persistence);
		this.serverURI = serverURI;
		final String methodName = "MqttWebSocketAsyncClient";
		this.log = LoggerFactory.getLogger(LoggerFactory.MQTT_CLIENT_MSG_CAT,(loggerName == null || loggerName.length() == 0) ? CLASS_NAME: loggerName);
		if (log.isLoggable(Logger.FINE)) {
			log.fine(CLASS_NAME, methodName, "101", new Object[] { clientId,serverURI, persistence });
		}
	}
	public MqttWebSocketAsyncClient(String serverURI,String clientID,MqttClientPersistence persistence) throws MqttException
	{
		this(serverURI,clientID,persistence,null);
	}
	public MqttWebSocketAsyncClient(String serverURI,String clientID,String loggerName) throws MqttException
	{
		this(serverURI,clientID,new MqttDefaultFilePersistence(),loggerName);
	}
	public MqttWebSocketAsyncClient(String serverURI,String clientID) throws MqttException
	{
		this(serverURI,clientID,(String)null);
	}
	
	@Override
	protected NetworkModule[] createNetworkModules(String address,MqttConnectOptions options) throws MqttException,MqttSecurityException
	{
		final String methodName = "createNetworkModules";
		if(log.isLoggable(Logger.FINE))
		{
			log.fine(CLASS_NAME, methodName, "116", new Object[]{address});
		}
		NetworkModule[] networkModules = null;
		String [] serverURIs = options.getServerURIs();
		String [] array = null;
		if(serverURIs == null)
		{
			array = new String[] {address};
		}
		else if(serverURIs.length == 0)
		{
			array = new String[] {address};
		}
		else
		{
			array = serverURIs;
		}
		networkModules = new NetworkModule[array.length];
		for(int i=0;i<array.length;i++)
		{
			networkModules[i] = createNetworkModule(array[i],options);
		}
		log.fine(CLASS_NAME, methodName, "100");
		return networkModules;
	}
	
	protected NetworkModule createNetworkModule(String input, MqttConnectOptions options) throws MqttException,MqttSecurityException
	{
		final String address = isDummyURI(input) ? this.serverURI : input;
		if(!address.startsWith("ws:") && !address.startsWith("wss:"))
		{
			return super.createNetworkModules(address, options)[0];
		}
		final String methodName = "createNetworkModule";
		if(log.isLoggable(Logger.FINE))
		{
			log.fine(CLASS_NAME, methodName, "115",new Object[]{address});
		}
		return newWebSocketNetworkModule(URI.create(address),"mqttv3.1",options);
	}
	protected NetworkModule newWebSocketNetworkModule(URI uri,String subProtocol,MqttConnectOptions options)
	{
		final WebSocketNetworkModule netModule = new WebSocketNetworkModule(uri,subProtocol,getClientId());
		netModule.setConnectTimeout(options.getConnectionTimeout());
		return netModule;
	}
}
