package ru.es.net.tcp;

import ru.es.log.Log;
import ru.es.thread.RunnableImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class RawServer
{
    public static int clientsAcceptSleepTime = 100;
    public static int usersSleepTime = 10;

    public final int port;
    public ServerSocket serverSocket;
    public boolean runing = true;

    public RawServer(int port)
    {
        this.port = port;
    }

    public ConcurrentLinkedQueue<Client> users = new ConcurrentLinkedQueue<>();
    
    public void start()
    {
        Thread socketAccpetThread = new Thread(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                try
                {
                    serverSocket = new ServerSocket(port);
                }
                catch (IOException e)
                {
                    Log.warning("RawServer: Couldn't listen to port "+port);
                    e.printStackTrace();
                    try
                    {
                        if (serverSocket != null)
                            serverSocket.close();
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                    return;
                }

                try
                {
                    Log.warning("RawServer: Waiting for a client on port: "+port);

                    while (runing)
                    {
                        Socket socket = serverSocket.accept();

                        Log.warning("RawServer: Client connected");
                        try
                        {
                            users.add(createClient(socket));
                            Thread.sleep(clientsAcceptSleepTime);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                catch (Exception e)
                {
                    Log.warning("RawServer: Can't accept");
                    users.clear();
                    close();
                }
            }
        });
        socketAccpetThread.setName("Raw Server");
        socketAccpetThread.start();

        Thread userThread = new Thread(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                while (runing)
                {
                    for (Client u : users)
                    {
                        if (u.socket.isConnected())
                        {
                            try
                            {
                                u.process();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                                closedClients.add(u);
                            }
                        }
                        else
                            closedClients.add(u);
                    }
                    if (!closedClients.isEmpty())
                    {
                        for (Client u : closedClients)
                        {
                            users.remove(u);
                            try
                            {
                                u.socket.close();
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        closedClients.clear();
                    }
                    Thread.sleep(usersSleepTime);
                }
            }
        });
        userThread.setName("Raw Server Users Thread");
        userThread.start();
    }

    ArrayList<Client> closedClients = new ArrayList<>();

    public void close()
    {
        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        for (Client u : users)
        {
            try
            {
                u.socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        users.clear();
    }

    public abstract Client createClient(Socket socket);

    public static abstract class Client
    {
        public final Socket socket;
        RawReader rawReader;

        public Client(Socket socket)
        {
            this.socket = socket;
            rawReader = createUserPacketHandler(this);
        }

        public void process() throws IOException
        {
            rawReader.read(socket.getInputStream());
        }

        protected abstract RawReader createUserPacketHandler(Client user);
    }


}
