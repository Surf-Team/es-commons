package ru.es.models;



import java.nio.ByteBuffer;

public class ByteFloatBuffer implements FloatBuffer
{
    public final ByteBuffer byteBuffer;

    public ByteFloatBuffer(ByteBuffer buffer)
    {
        this.byteBuffer = buffer;
    }

    @Override
    
    public final int limit()
    {
        return byteBuffer.limit();
    }

    @Override
    
    public final void position(int pos)
    {
        byteBuffer.position(pos);
    }

    @Override
    
    public final float getFloat()
    {
        return byteBuffer.getFloat();
    }

    
    public void putFloat(float f)
    {
        byteBuffer.putFloat(f);
    }
}
