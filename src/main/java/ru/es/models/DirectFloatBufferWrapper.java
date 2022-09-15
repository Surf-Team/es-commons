package ru.es.models;



import java.nio.ByteBuffer;

public class DirectFloatBufferWrapper
{
    public ByteBuffer buffer;

    public DirectFloatBufferWrapper(int length)
    {
        allocate(length);
    }

    
    public void allocate(int length)
    {
        buffer = ByteBuffer.allocateDirect(length*4);
    }

    
    public void position(int i)
    {
        buffer.position(i*4);
    }

    public float positionAndGet(int i)
    {
        buffer.position(i*4);
        return buffer.getFloat();
    }

    
    public int limit()
    {
        return buffer.limit() / 4;
    }

    
    public void putFloat(float f)
    {
        buffer.putFloat(f);
    }

    
    public float getFloat()
    {
        return buffer.getFloat();
    }

    
    public void set(float[] array)
    {
        buffer.getFloat(5);

        buffer.position(0);
        for (int i = 0; i < limit(); i++)
        {
            buffer.putFloat(array[i]);
        }
    }

    
    public float get(int index)
    {
        return buffer.getFloat(index*4);
    }

    public void setAndMult(float[] array, float mult)
    {
        buffer.position(0);
        for (int i = 0; i < limit(); i++)
        {
            buffer.putFloat(array[i] * mult);
        }
    }
}
