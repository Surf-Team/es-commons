package ru.es.models;



public class ESFixedFastFloatQueue
{
    final float[] array;
    final int limit;
    int offset = 0;

    
    public ESFixedFastFloatQueue(int limit)
    {
        array = new float[limit];
        this.limit = limit;
    }

    
    public void add(float f)
    {
        array[offset] = f;
        offset++;
        if (offset >= limit)
            offset = 0;
    }

    
    public float get(int ago)
    {
        int index = offset - ago;
        if (index < 0)
            index+= limit;
        return array[index];
    }
}
