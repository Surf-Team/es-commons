package ru.es.net;

import ru.es.lang.ESEventHandler;
import ru.es.lang.ESGetter;
import ru.es.log.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TcpObjectStreamServer
{
	String serverAddr;
	int port;
	ServerSocket serverSocket;

	ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<User>();
	ArrayList<User> usersCopy = new ArrayList<>();

	// private
	private int maxId = 0;
	private long lastService = System.currentTimeMillis();
	private boolean running = false;

	// settings
	public long USER_MAX_INACTIVE = 5*60*1000;
	public int USER_READ_BUFFER_SIZE = 512*1024;
	public int BACKLOG = 1000;
	public String SERVER_LOG_NAME = "TcpServer";
	public boolean USER_CLOSE_ON_FAIL = true;
	public int SERVICE_DELAY = 5*1000;
	public int PROCESS_USERS_DELAY = 100;
	public boolean DEBUG = false;
	public int FREE_MEMORY_LIMIT = 64*1024*1024;
	public boolean GC_ON_SERVICE = true;
	public final ESEventHandler<Object> onObjectReceived = new ESEventHandler<>();

	public long pingSent = 0;

	public ESEventHandler<User> userClosed = new ESEventHandler<>();

	public ESGetter<ClassLoader> additionalClassLoader = null;

	public TcpObjectStreamServer(String serverAddr, int serverPort)
	{
		this.serverAddr = serverAddr;
		this.port = serverPort;
	}

	public void start() throws IOException
	{
		running = true;


		try
		{
			serverSocket = new ServerSocket(port,BACKLOG);
		}
		catch (IOException e)
		{
			Log.warning(SERVER_LOG_NAME+": Couldn't listen to port "+port+": "+e.getMessage());
			close();
			throw e;
		}

		Thread serverSocketThread = new Thread(()->
		{
			Log.warning(SERVER_LOG_NAME+": Waiting for a client on port: "+port);

			while (running)
			{
				Socket newClient = null;

				try
				{
					long allocatedMemory = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					long presumableFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;

					//Log.warning("presumableFreeMemory: "+presumableFreeMemory);

					//if (users.size() < maxProcessingUsers)
					if (presumableFreeMemory > FREE_MEMORY_LIMIT)
					{
						newClient = serverSocket.accept();
						if (allowAddUser(newClient))
						{
							createUser(newClient);
						}
					}
					else
					{
						Log.warning("Memory exceed! presumableFreeMemory: "+presumableFreeMemory+". Wait for GC... Users: " + users.size() + "");

						try
						{
							Thread.sleep(500);
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				catch (OutOfMemoryError e)
				{
					e.printStackTrace();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		});
		serverSocketThread.setName(SERVER_LOG_NAME+"_AcceptSocket");
		serverSocketThread.start();

		Thread usersReadThread = new Thread(()->
		{
			while (running)
			{
				processUsers();
				if (PROCESS_USERS_DELAY > 0)
				{
					try
					{
						Thread.sleep(PROCESS_USERS_DELAY);
					}
					catch (InterruptedException e)
					{
						break;
					}
				}
			}
		});
		usersReadThread.setName(SERVER_LOG_NAME+"_ReadPackets");
		usersReadThread.start();
	}

	private void createUser(Socket newClient) throws IOException
	{
		User u = new User(newClient, maxId);
		//Log.warning(SERVER_LOG_NAME+": Client connected: "+u.id+", "+ newClient.getInetAddress().getHostAddress() + ", users size: " + (users.size()+1));
		users.add(u);
		maxId++;
	}

	public void close()
	{
		running = false;

		try
		{
			if (serverSocket != null)
				serverSocket.close();
		}
		catch (Exception e)
		{
			Log.warning(SERVER_LOG_NAME+": Cant close");
		}
		users.clear();
		Log.warning(SERVER_LOG_NAME+": Closed");
	}

	private void processUsers()
	{
		usersCopy.clear();
		usersCopy.addAll(users);
		for (User u : usersCopy)
		{
			u.receivePackets();
		}

		if (lastService + SERVICE_DELAY < System.currentTimeMillis())
		{
			usersCopy.clear();
			usersCopy.addAll(users);
			for (User u : usersCopy)
			{
				if (u.lastActive + USER_MAX_INACTIVE < System.currentTimeMillis())
				{
					u.close(CloseReason.Inactivity);
				}
			}


			lastService = System.currentTimeMillis();
		}
	}

	protected boolean allowAddUser(Socket socket)
	{
		return true;
	}

	public ConcurrentLinkedQueue<User> getUsers()
	{
		return users;
	}


	public class User
	{
		public int id;
		public HttpConnectionState state = HttpConnectionState.PARSING_HEADER;
		public Object exchange;
		byte[] readBuffer = new byte[USER_READ_BUFFER_SIZE];
		int pos = 0;
		public long lastActive = System.currentTimeMillis();
		public final String ip;
		private Socket _socket;
		public final ESEventHandler<Object> onObjectReceivedToUser = new ESEventHandler<>();

		ObjectOutputStream oos;
		ObjectInputStream ois;

		public TcpObjectStreamServer server = TcpObjectStreamServer.this;

		public final ConcurrentLinkedQueue<Object> sendQueue = new ConcurrentLinkedQueue<>();


		User(Socket socket, int id) throws IOException
		{
			_socket = socket;
			this.id = id;
			this.ip = socket.getInetAddress().getHostAddress();
			oos = new ObjectOutputStream(socket.getOutputStream());

			createInputStream();
		}

		public void stopInputStream() throws IOException
		{
			ois.close();
		}


		public void createInputStream() throws IOException
		{
			Log.warning("TcpObjectStreamServer: recreate output stream");
			ois = new ObjectInputStream(_socket.getInputStream())
			{
				@Override
				protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
					String className = desc.getName();

					if (additionalClassLoader != null)
					{
						try
						{
							//Thread.dumpStack();
							var cl = additionalClassLoader.get();
							//Log.warning("loading class from class loader: "+className+", "+cl);
							return Class.forName(className, false, cl);
						}
						catch (ClassNotFoundException e)
						{
							Log.warning("failed!");
						}
					}

					return super.resolveClass(desc);
				}
			};
		}

		public void send(Object packet) throws IOException
		{
			oos.writeObject(packet);
		}

		public void close(CloseReason closeReason)
		{
			if (DEBUG)
				Log.warning(SERVER_LOG_NAME+": Closing the user "+id+" due "+closeReason);

			users.remove(this);
			userClosed.event(this);
			if (_socket != null && !_socket.isClosed())
			{
				try
				{
					_socket.close();
				}
				catch (IOException e)
				{
					Log.warning(SERVER_LOG_NAME+": fail when closing User "+id);
					e.printStackTrace();
				}
			}
		}

		public void addEvent(Object packetEvent)
		{
			sendQueue.offer(packetEvent);
		}


		public void receivePackets()
		{
			try
			{
				Object read = ois.readObject();

				//Log.warning("read object!");

				//Log.warning("Object class loader: "+read.getClass().getClassLoader().toString());
				onObjectReceivedToUser.event(read);
				onObjectReceived.event(read);
			}
			catch (SocketException e)
			{
				state = HttpConnectionState.NONE;
				Log.warning(SERVER_LOG_NAME+": Socket error: " + id+". Error: "+e.getMessage());
				//e.printStackTrace();
				if (USER_CLOSE_ON_FAIL)
					close(CloseReason.ServerCloseError);
			}
			catch (Exception e)
			{
				state = HttpConnectionState.NONE;
				Log.warning(SERVER_LOG_NAME+": User loop failed: " + id+". Error: "+e.getMessage());
				e.printStackTrace();
				if (USER_CLOSE_ON_FAIL)
					close(CloseReason.ServerCloseError);
			}

			if (_socket.isClosed())
				close(CloseReason.SocketClosedByExternal);
		}
	}


	public enum CloseReason
	{
		Inactivity,
		ServerCloseError,
		SocketClosedByExternal,
		ManualClose,
		PerformanceSecurity;

	}


}