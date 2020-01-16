package ru.es.net;

import ru.es.jfx.binding.ESProperty;
import ru.es.log.Log;
import ru.es.thread.UnloadableThreadPoolManager;
import ru.es.thread.RunnableImpl;
import javolution.util.FastTable;

import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Created by sara on 20.04.2015.
 */
public abstract class TCPByteClient
{
    ESProperty<String> serverAddr;
    ESProperty<String> port;

    InputStream in;
    public OutputStream out;
    public long sleepTime = 1;

    Socket fromServerSocket;

    NetworkThread thread;

    public TCPByteClient(String serverAddr, int serverPort)
    {
        this.serverAddr = new ESProperty<>(serverAddr);
        this.port = new ESProperty<>(""+serverPort);

        thread = new NetworkThread();
        UnloadableThreadPoolManager.getInstance().executeTask(thread);
    }

    public TCPByteClient(ESProperty<String> serverAddr, ESProperty<String> serverPort)
    {
        this.serverAddr = serverAddr;
        this.port = serverPort;

        thread = new NetworkThread();
        UnloadableThreadPoolManager.getInstance().executeTask(thread);
    }

    private boolean runing = true;

    private class NetworkThread extends RunnableImpl
    {
        @Override
        public void runImpl()
        {
            connect();
        }
    }


    public boolean connect()
    {
        Log.warning("TCPClient: Connecting to " + serverAddr + ":" + port);

        try
        {
            fromServerSocket = new Socket(serverAddr.get(), Integer.parseInt(port.getValue()));

            in  = new BufferedInputStream(fromServerSocket.getInputStream());
            out = new BufferedOutputStream(fromServerSocket.getOutputStream());
        }
        catch (Exception e)
        {
            Log.warning("TCPClient: Can't connect (4)");
            close();
            return false;
        }

        onConnected();

        Log.warning("TCPClient: Wait for messages");

        try
        {
            while (runing)
            {
                List<Integer> bytes = new FastTable<>();
                while (in.available() > 0)
                {
                    bytes.add(in.read());
                }
                if (!bytes.isEmpty())
                {
                    packetReceived(bytes);
                    bytes.clear();
                }

                if (sleepTime > 0)
                {
                    try
                    {
                        Thread.sleep(sleepTime);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        catch (IOException e)
        {
            Log.warning("TCPClient: Can't read");
            close();
        }
        return true;
    }

    public abstract void packetReceived(List<Integer> packet);

    public abstract void onConnected();

    public void sendPacket(byte[] packet) throws IOException
    {
        if (out != null)
        {
            out.write(packet);
            out.flush();
        }
    }

    public Socket getSocket()
    {
        return fromServerSocket;
    }

    public void close()
    {
        if (thread != null)
            runing = false;

        try
        {
            if (out != null)
                out.close();

            if (in != null)
                in.close();

            if (fromServerSocket != null)
                fromServerSocket.close();
        }
        catch (Exception e)
        {
            Log.warning("TCPClient: Cant close");
        }
        Log.warning("TCPClient: Closed");
        closed();
    }

    public void closed()
    {

    }

}
