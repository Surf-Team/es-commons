package ru.es.net;

import ru.es.log.Log;
import ru.es.thread.SimpleThreadPool;
import ru.es.thread.UnloadableThreadPoolManager;
import ru.es.thread.RunnableImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 17.11.14
 * Time: 4:25
 * To change this template use File | Settings | File Templates.
 */
public abstract class TCPServer
{
    String serverAddr;
    int port;
    ClientsCatchingThread catchThread;
    ServerSocket serverSocket;
    SimpleThreadPool threadPool = new SimpleThreadPool("SimpleThreadPool", 2);

    public TCPServer(String serverAddr, int serverPort)
    {
        this.serverAddr = serverAddr;
        this.port = serverPort;

        catchThread = new ClientsCatchingThread();
        UnloadableThreadPoolManager.getInstance().executeTask(catchThread);
    }

    public void close()
    {
        catchThread.runing = false;

        try
        {
            if (serverSocket != null)
                serverSocket.close();
        }
        catch (Exception e)
        {
            Log.warning("TCPConnection: Cant close");
        }
        Log.warning("TCPConnection: Closed");
    }

    private class ClientsCatchingThread extends RunnableImpl
    {
        private boolean runing = true;

        @Override
        public void runImpl()
        {
            try
            {
                serverSocket = new ServerSocket(port);
            }
            catch (IOException e)
            {
                Log.warning("TCPConnection: Couldn't listen to port "+port);
                close();
                return;
            }

            try
            {
                Log.warning("TCPConnection: Waiting for a client on port: "+port);

                while (runing)
                {
                    Socket fromClientSocket = serverSocket.accept(); // block...

                    addUser(fromClientSocket);

                    Log.warning("TCPConnection: Client connected");
                }
            }
            catch (IOException e)
            {
                Log.warning("TCPConnection: Can't accept");
                close();
                return;
            }
        }
    }

    public boolean allowAddUser(Socket socket)
    {
        return true;
    }

    private void addUser(Socket socket)
    {
        if (allowAddUser(socket))
            users.add(new User(socket));
    }

    ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<User>();

    public ConcurrentLinkedQueue<User> getUsers()
    {
        return users;
    }

    public void broadcastMessage(String message)
    {
        threadPool.executeTask(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                for (TCPServer.User user : getUsers())
                {
                    try
                    {
                        if (user != null && user.getSocket().isConnected())
                        {
                            user.sendPacket(message);
                        }
                    }
                    catch (Exception e)
                    {
                        Log.warning("TCPServer: Error when broadcast message");
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static boolean closeOnFail = false;

    public class User
    {
        Socket socket;
        BufferedReader in;
        PrintWriter out;

        User(Socket socket)
        {
            this.socket = socket;

            try
            {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            }
            catch (IOException e)
            {
                Log.warning("TCPConnection: fail when accept User. It may be crash the server loop");
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            RunnableImpl loop = new RunnableImpl()
            {
                @Override
                public void runImpl()
                {
                    String input;
                    try
                    {
                        while ((input = in.readLine()) != null)
                        {
                            packetReceived(User.this, input);
                        }
                    }
                    catch (IOException e)
                    {
                        Log.warning("TCPConnection: User loop failed");
                        e.printStackTrace();
                        if (closeOnFail)
                            close();
                    }
                }
            };
            UnloadableThreadPoolManager.getInstance().executeTask(loop);
        }

        public void sendPacket(String packet)
        {
            if (out != null)
                out.println(packet);
        }

        public void close()
        {
            Log.warning("TCPConnection: Closing the user");
            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                    Log.warning("TCPConnection: fail when closing User");
                    e.printStackTrace();
                }
            }
        }

        public Socket getSocket()
        {
            return socket;
        }
    }

    public abstract void packetReceived(User user, String packet);

}
