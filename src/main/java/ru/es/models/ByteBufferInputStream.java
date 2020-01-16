package ru.es.models;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class ByteBufferInputStream extends InputStream
{
    ByteBuffer buffer;

    public ByteBufferInputStream(ByteBuffer buffer)
    {
        this.buffer = buffer;
        buffer.position(0);
    }

    @Override
    public int read() throws IOException
    {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        return buffer.get() & 0xFF;
    }

    public int available() throws IOException
    {
        return buffer.limit() - buffer.position();
    }
}
