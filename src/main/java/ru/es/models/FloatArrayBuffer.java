package ru.es.models;



public class FloatArrayBuffer implements FloatBuffer
{
    public final float[] array;
    private int position;

    
    public FloatArrayBuffer(int floatSize)
    {
        this.array = new float[floatSize];
    }

    
    public FloatArrayBuffer(float[] array)
    {
        this.array = array;
    }


    @Override
    
    public int limit()
    {
        return array.length * 4;
    }

    @Override
    
    public void position(int pos)
    {
        this.position = pos;
    }

    @Override
    
    public float getFloat()
    {
        float ret = array[position/4];
        position+=4;
        return ret;
    }

    @Override
    
    public void putFloat(float f)
    {
        array[position/4] = f;
        position+=4;
    }
}
