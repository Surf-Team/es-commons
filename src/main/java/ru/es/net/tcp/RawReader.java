package ru.es.net.tcp;

import ru.es.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class RawReader
{
    int packetLength = 0;
    boolean isPacketParsing = false;
    Packet currentPacket = null;
    ByteBuffer byteBuffer;
    private static final int HEADER_SIZE = 1+2;
    private Map<Byte, Packet> packets = new HashMap<>();
    String name;

    public RawReader(int readBufferSize, String name) // 1024*32 default
    {
        byteBuffer = ByteBuffer.allocate(readBufferSize);
        this.name = name;
    }

    public interface Packet
    {
        void read(ByteBuffer from);
    }

    public void read(InputStream inputStream) throws IOException
    {
        int prePos = byteBuffer.position();
        boolean read = false;
        while (inputStream.available() > 0)
        {
            byteBuffer.put((byte) inputStream.read());
            read = true;
        }
        if (read)
            Log.warning(name+": read "+(byteBuffer.position() - prePos)+" bytes");

        if (!isPacketParsing)
        {
            if (byteBuffer.position() >= HEADER_SIZE)
            {
                int oldPos = byteBuffer.position();

                byteBuffer.position(0);
                packetLength = byteBuffer.getShort();
                byte header = byteBuffer.get();
                Log.warning("RawReader "+name+" packet received length: "+packetLength+", header: "+header);
                currentPacket = packets.get(header);
                isPacketParsing = true;

                byteBuffer.position(oldPos);
            }
        }

        if (isPacketParsing)
        {
            if (byteBuffer.position() >= packetLength)
            {
                Log.warning(name+": byteBuffer.position(): "+byteBuffer.position()+", packetLength: "+packetLength);
                long lastPosition = byteBuffer.position();
                byteBuffer.position(HEADER_SIZE);
                if (currentPacket != null)
                {
                    currentPacket.read(byteBuffer);
                    if (byteBuffer.position() != packetLength)
                    {
                        Thread.dumpStack();
                        Log.warning(name + ": WRONG READ buffer. POS: " + byteBuffer.position() + " != " + packetLength);
                    }
                }
                // если в буффере есть ещё данные
                if (lastPosition > packetLength)
                {
                    int secondPacketPos = packetLength;
                    long remainsSize = lastPosition - packetLength;
                    Log.warning(name+": Два пакета. old packet len: "+packetLength+", remainsSize: "+remainsSize);
                    for (int i = 0; i < remainsSize; i++)
                    {
                        byteBuffer.array()[i] = byteBuffer.array()[i+secondPacketPos];
                    }
                    byteBuffer.position((int) remainsSize);
                }
                else
                    byteBuffer.position(0);

                isPacketParsing = false;
            }
        }
    }


    public void registerPacket(byte id, Packet packet)
    {
        packets.put(id, packet);
    }
}
