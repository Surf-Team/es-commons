package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;

public class ESFixedFastFloatQueue
{
    final float[] array;
    final int limit;
    int offset = 0;

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public ESFixedFastFloatQueue(int limit)
    {
        array = new float[limit];
        this.limit = limit;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void add(float f)
    {
        array[offset] = f;
        offset++;
        if (offset >= limit)
            offset = 0;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float get(int ago)
    {
        int index = offset - ago;
        if (index < 0)
            index+= limit;
        return array[index];
    }
}
