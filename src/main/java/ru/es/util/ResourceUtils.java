package ru.es.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class ResourceUtils
{
    // "/file.txt"
    public static String readResourceToString(String path) throws IOException
    {
        //InputStream is = getResourceAsStream(path);
        InputStream is = ResourceUtils.class.getResourceAsStream(path);
        if (is == null)
            return null;
        byte[] buffer = new byte[is.available()];
        is.read(buffer, 0, buffer.length);

        return new String(buffer);
    }
}
