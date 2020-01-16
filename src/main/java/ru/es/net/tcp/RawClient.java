package ru.es.net.tcp;

import ru.es.log.Log;
import ru.es.thread.RunnableImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class RawClient
{
    public enum Result
    {
        CantConnect,
        Success
    }

    final RawReader rawReader;

    public RawClient(RawReader rawReader)
    {
        this.rawReader = rawReader;
    }

    private boolean running = true;
    private int delay = 100;

    public Socket socket = null;

    public int timeout = 5;

    public Result connect(String serverAddr, int port)
    {
        running = true;
        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddr, port), timeout*1000);
        }
        catch (IOException e)
        {
            try
            {
                socket.close();
            }
            catch (IOException e1)
            {
                e1.printStackTrace();
            }
            return Result.CantConnect;
        }

        Thread t = new Thread(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                while (running)
                {
                    try
                    {
                        if (!socket.isClosed())
                            rawReader.read(socket.getInputStream());

                        Thread.sleep(delay);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        running = false;
                    }
                }
                try
                {
                    socket.close();
                    //socket.close();
                    Log.warning("Socket closed");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        return Result.Success;
    }



    public void close()
    {
        running = false;
    }

    public void sendPacket(byte[] b) throws IOException
    {
        socket.getOutputStream().write(b);
    }

}
