package ru.es.net;

import ru.es.lang.ESEventHandler;
import ru.es.log.Log;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;

public class TcpObjectStreamClient
{
	private final String serverAddr;
	private final int port;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Socket fromServerSocket;
	public ESEventHandler<Object> onPacketReceived = new ESEventHandler<>();

	public boolean DEBUG;
	public boolean runing = true;
	private long processPacketsDelay = 1;

	public TcpObjectStreamClient(String serverAddr, int port, boolean debug)
	{
		this.serverAddr = serverAddr;
		this.port = port;
		this.DEBUG = debug;
	}

	public void tryConnect()
	{
		while (true)
		{
			try
			{
				Log.warning("creating connection...");
				createConnection();
			}
			catch (ConnectException e)
			{
				Log.warning(e.getMessage());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			Log.warning("Reconnect to "+serverAddr+":"+port+" after 1 sec...");
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				break;
			}
		}
	}

	private void createConnection() throws IOException, InterruptedException
	{
		fromServerSocket = new Socket(serverAddr, port);

		ois = new ObjectInputStream(fromServerSocket.getInputStream());
		oos = new ObjectOutputStream(fromServerSocket.getOutputStream());

		//if (DEBUG)
			Log.warning("TCPClient: Wait for messages");

		onConnected();
	}

	public void receivePackets() throws IOException
	{
		try
		{
			Object o = ois.readObject();
			onPacketReceived.event(o);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	public void onConnected() throws IOException, InterruptedException
	{
		Log.warning("Connected to target server!");
		while (runing)
		{
			receivePackets();
			Thread.sleep(processPacketsDelay);
		}
	}

	public synchronized void sendPacket(Object object) throws IOException
	{
		if (oos != null)
			oos.writeObject(object);
		else
			Log.warning("output stream == null!");
	}

	public boolean ready()
	{
		return oos != null;
	}
}
