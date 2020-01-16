package ru.es.util;

import java.io.*;

/**
 * Created by saniller on 01.07.2016.
 */
public class StreamUtils
{
    public static InputStream getStreamAsResource(Object owner, String resource)
    {
        return owner.getClass().getResourceAsStream(resource);
    }

    public static String readToEnd(InputStream inputStream, String charset) throws IOException //"UTF-8"
    {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, charset);
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
