package ru.es.net.tcp;

import ru.es.log.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class RawPacket
{
    ByteBuffer bb;

    public RawPacket(byte byteHeader)
    {
        this(byteHeader, 512);
    }

    public RawPacket(byte byteHeader, int maxPacketSize)
    {
        bb = ByteBuffer.allocate(maxPacketSize);
        bb.put(byteHeader);
    }

    public void writeS(String s)
    {
        RawUtils.writeString(s, bb);
    }

    public void writeInt(int i)
    {
        bb.putInt(i);
    }

    public void writeByte(byte b)
    {
        bb.put(b);
    }

    public void send(OutputStream stream) throws IOException
    {
        bb.flip();
        RawUtils.writeShort(bb.limit()+2, stream);
        for (int i = 0; i < bb.limit(); i++)
            stream.write(bb.get());
        Log.warning("RawPacket sent size: "+(bb.limit()+2));
    }
}
