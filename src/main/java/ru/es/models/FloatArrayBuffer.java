package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;

public class FloatArrayBuffer implements FloatBuffer
{
    public final float[] array;
    private int position;

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public FloatArrayBuffer(int floatSize)
    {
        this.array = new float[floatSize];
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public FloatArrayBuffer(float[] array)
    {
        this.array = array;
    }


    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public int limit()
    {
        return array.length * 4;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void position(int pos)
    {
        this.position = pos;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float getFloat()
    {
        float ret = array[position/4];
        position+=4;
        return ret;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void putFloat(float f)
    {
        array[position/4] = f;
        position+=4;
    }
}
