package ru.es.net.tcp;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class RawUtils
{
    public static boolean rawDebug = false;

    public static String readString(ByteBuffer from)
    {
        int byteArraySize = from.getInt();
        byte[] b = new byte[byteArraySize];
        from.get(b);
        return new String(b);
    }

    public static void writeString(String str, ByteBuffer to)
    {
        byte[] b = str.getBytes();
        to.putInt(b.length);
        to.put(b);
    }
    public static void writeString(String str, OutputStream to) throws IOException
    {
        byte[] b = str.getBytes();
        ByteBuffer bb = ByteBuffer.allocate(4+b.length);
        bb.putInt(b.length);
        bb.put(b);
        to.write(bb.array());
    }

    public static void writeShort(int shortNum, OutputStream to) throws IOException
    {
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.putShort((short) shortNum);
        to.write(bb.array());
    }
}
