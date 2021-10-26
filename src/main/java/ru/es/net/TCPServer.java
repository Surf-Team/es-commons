package ru.es.net;

import ru.es.lang.ESEventHandler;
import ru.es.lang.limiters.CountTimeLimiter;
import ru.es.log.Log;
import ru.es.thread.SimpleThreadPool;
import ru.es.thread.RunnableImpl;
import ru.es.util.ByteUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
    ServerSocket serverSocket;

    public SimpleThreadPool executorThreadPool;
    ConcurrentLinkedQueue<User> users = new ConcurrentLinkedQueue<User>();
    ArrayList<User> usersCopy = new ArrayList<>();

    // private
    private int maxId = 0;
    private long lastService = System.currentTimeMillis();
    private boolean running = false;
    private byte[] lineFeed;

    // settings
    public long USER_MAX_INACTIVE = 5*60*1000;
    public int USER_READ_BUFFER_SIZE = 32*1024;
    public int USER_WRITE_BUFFER_SIZE = 64*1024;
    public int EXECUTOR_THREAD_COUNT = 2;
    public int BACKLOG = 1000;
    public String SERVER_LOG_NAME = "TcpServer";
    public boolean USER_CLOSE_ON_FAIL = true;
    public int SERVICE_DELAY = 5*1000;
    public int PROCESS_USERS_DELAY = 100;
    public boolean DEBUG = false;
    public int FREE_MEMORY_LIMIT = 64*1024*1024;
    public boolean GC_ON_SERVICE = true;

    public ESEventHandler<User> userClosed = new ESEventHandler<>();

    public TCPServer(String serverAddr, int serverPort)
    {
        this.serverAddr = serverAddr;
        this.port = serverPort;
    }

    public void start() throws IOException
    {
        running = true;
        executorThreadPool = new SimpleThreadPool(SERVER_LOG_NAME+"_UsersThreadPool", EXECUTOR_THREAD_COUNT);

        lineFeed = "\n".getBytes(Charset.defaultCharset());

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
                    long allocatedMemory = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
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

    private void createUser(Socket newClient)
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

            if (GC_ON_SERVICE)
            {
                long usedMemoryMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 / 1000;
                Log.warning("Service: usage RAM: " + (usedMemoryMB) + " MB");
                System.gc();
                usedMemoryMB = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000 / 1000;
                Log.warning("Service: usage RAM after GC: " + (usedMemoryMB) + " MB");
                Log.warning(SERVER_LOG_NAME + ": Clients connected for all time: " + maxId + ", users size now: " + (users.size() + 1));
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

    public void broadcastMessage(String message)
    {
        executorThreadPool.executeTask(new RunnableImpl() {
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
                        Log.warning(SERVER_LOG_NAME+": Error when broadcast message");
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public class User
    {
        public int id;
        Socket socket;
        //BufferedReader in;
        PrintWriter out;
        byte[] readBuffer = new byte[USER_READ_BUFFER_SIZE];
        int pos = 0;
        public long lastActive = System.currentTimeMillis();
        public final String ip;

        User(Socket socket, int id)
        {
            this.id = id;
            this.socket = socket;
            this.ip = socket.getInetAddress().getHostAddress();

            try
            {
                out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream(), Charset.defaultCharset()),
                        USER_WRITE_BUFFER_SIZE),
                        true);

                /*in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.defaultCharset()),
                        USER_READ_BUFFER_SIZE);*/
            }
            catch (IOException e)
            {
                Log.warning(SERVER_LOG_NAME+": fail when accept User "+id);
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        public void sendPacket(String packet)
        {
            if (out != null)
                out.println(packet);
        }

        public void close(CloseReason closeReason)
        {
            if (DEBUG)
                Log.warning(SERVER_LOG_NAME+": Closing the user "+id+" due "+closeReason);
            
            users.remove(this);
            userClosed.event(this);
            if (socket != null && !socket.isClosed())
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                    Log.warning(SERVER_LOG_NAME+": fail when closing User "+id);
                    e.printStackTrace();
                }
            }
        }

        public Socket getSocket()
        {
            return socket;
        }

        public void receivePackets()
        {
            int available = 0;
            try
            {
                if (socket.getInputStream().available() > 0)
                {
                    lastActive = System.currentTimeMillis();
                    available = socket.getInputStream().available();


                    socket.getInputStream().read(readBuffer, pos, available);
                    pos += available;

                    boolean containsNewLine = ByteUtils.contains(readBuffer, lineFeed, true, pos);

                    if (containsNewLine)
                    {
                        String s = new String(readBuffer, 0, pos-lineFeed.length);
                        pos = 0;
                        if (DEBUG)
                            Log.warning(SERVER_LOG_NAME+": Packet received: "+s);
                        packetThreadRoute(this, s);
                    }
                    else
                        Log.warning(SERVER_LOG_NAME+": Waiting for full packet. User "+id);
                }

                /*String input = in.readLine();
                if (input != null)
                {
                    lastActive = System.currentTimeMillis();
                    executorThreadPool.execute(()->packetReceived(User.this, input));
                } */
            }
            catch (IOException e)
            {
                Log.warning(SERVER_LOG_NAME+": User loop failed: " + id);
                e.printStackTrace();
                if (USER_CLOSE_ON_FAIL)
                    close(CloseReason.ServerCloseError);
            }
            catch (IndexOutOfBoundsException e)
            {
                Log.warning(SERVER_LOG_NAME+": User loop failed: " + id+". User read buffer limit. Available bytes in socket: "+available);
                //e.printStackTrace();
                if (USER_CLOSE_ON_FAIL)
                    close(CloseReason.ServerCloseError);
            }
            catch (Exception e)
            {
                Log.warning(SERVER_LOG_NAME+": User loop failed: " + id+". Unhandled error: "+e.getMessage());
                e.printStackTrace();
                if (USER_CLOSE_ON_FAIL)
                    close(CloseReason.ServerCloseError);
            }

            if (socket.isClosed())
                close(CloseReason.SocketClosedByExternal);
        }
    }

    protected void packetThreadRoute(User user, String s)
    {
        executorThreadPool.execute(() ->
        {
            try
            {
                packetReceived(user, s);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    public enum CloseReason
    {
        Inactivity,
        ServerCloseError,
        SocketClosedByExternal,
        ManualClose,
        PerformanceSecurity;

    }

    public abstract void packetReceived(User user, String packet);

}
