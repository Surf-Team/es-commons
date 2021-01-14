package ru.es.net;

import ru.es.log.Log;
import ru.es.thread.SingletonThreadPool;
import ru.es.thread.RunnableImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;


/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 17.11.14
 * Time: 3:56
 * To change this template use File | Settings | File Templates.
 */
public abstract class TCPClient
{
    String serverAddr;
    int port;

    BufferedReader in;
    public PrintWriter out;

    public Socket fromServerSocket;

    NetworkThread thread;

    public TCPClient(String serverAddr, int serverPort)
    {
        this.serverAddr = serverAddr;
        this.port = serverPort;

        connect();
    }

    public void connect()
    {
        thread = new NetworkThread();
        executeThread(thread);
    }

    public void executeThread(RunnableImpl r)
    {
        SingletonThreadPool.getInstance().executeTask(r);
    }

    private class NetworkThread extends RunnableImpl
    {
        public boolean runing = true;

        @Override
        public void runImpl()
        {
            Log.warning("TCPClient: Connecting to " + serverAddr + ":" + port);

            try
            {
                fromServerSocket = new Socket(serverAddr, port);
                Log.warning("Created fromServerSocket");
                InputStreamReader inputStream = new InputStreamReader(fromServerSocket.getInputStream(), "UTF-8");
                in  = new BufferedReader(inputStream);
                out = new PrintWriter(fromServerSocket.getOutputStream(), true);
            }
            catch (IOException e)
            {
                Log.warning("TCPClient: Can't connect (4)");
                close();
                return;
            }

            onConnected();

            Log.warning("TCPClient: Wait for messages");
            String input;
            try
            {
                while (runing && (input = in.readLine()) != null)
                {
                    final String msg = input;

                    SingletonThreadPool.getInstance().executeTask(new RunnableImpl()
                    {
                        @Override
                        public void runImpl()
                        {
                            try
                            {
                                packetReceived(msg);
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            }
            catch (IOException e)
            {
                Log.warning("TCPClient: Can't read");
                close();
            }
        }
    }

    public abstract void packetReceived(String packet);

    public abstract void onConnected();

    public void sendPacket(String packet)
    {
        if (out != null)
            out.println(packet);
    }

    public Socket getSocket()
    {
        return fromServerSocket;
    }

    public void close()
    {
        if (thread != null)
            thread.runing = false;

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
    }

}
