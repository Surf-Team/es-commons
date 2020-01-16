package ru.es.net;

import ru.es.lang.ESSetter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class NetUtils
{
    public static void downloadFileFromURL(String urlString, File destination) throws IOException
    {
        URL website = new URL(urlString);
        ReadableByteChannel rbc;
        rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destination);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    // download using NIO
    public static void downloadFileFromURL(String urlString, File destination,
                                           ESSetter<FileOutputStream> streamSetter) throws IOException
    {
        URL website = new URL(urlString);
        ReadableByteChannel rbc;
        InputStream inputStream = website.openStream();
        rbc = Channels.newChannel(inputStream);
        FileOutputStream fos = new FileOutputStream(destination);
        streamSetter.set(fos);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }

    // качаем файл с помощью Stream
    public static void downloadUsingStream(String urlString, File destination, ESSetter<FileOutputStream> streamSetter) throws IOException
    {
        URL url = new URL(urlString);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(destination);
        streamSetter.set(fis);
        byte[] buffer = new byte[1024*16];
        int count=0;
        while((count = bis.read(buffer,0,1024*16)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    public static int getFileSize(URL url) {
        URLConnection conn = null;
        try {
            conn = url.openConnection();
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).setRequestMethod("HEAD");
            }
            conn.getInputStream();
            return conn.getContentLength();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(conn instanceof HttpURLConnection) {
                ((HttpURLConnection)conn).disconnect();
            }
        }
    }
}
