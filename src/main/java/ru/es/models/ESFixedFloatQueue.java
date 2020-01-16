package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;

import java.util.ArrayList;
import java.util.Collection;

public class ESFixedFloatQueue
{
    public final int limit;
    public float[] keyArray;   // можно обращаться напрямую
    int currentSize = 0;

    public ESFixedFloatQueue(float[] initArray)
    {
        this.keyArray = initArray;
        this.limit = initArray.length;
    }

    // добавляет в начало очереди, смещая последующие (медленно)
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void add(float key)
    {
        // сдвиг элементов вправо
        for (int i = currentSize-1; i >= 0; i--)
        {
            if (i+1 >= limit)
                continue;

            keyArray[i+1] = keyArray[i];
        }

        keyArray[0] = key;

        if (currentSize < limit)
            currentSize++;
    }

    // удаляет все элементы, которые попадутся с таким ключём
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void remove(float key)
    {
        int found = 0;
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == key)
            {
                found++;
            }
            else if (found > 0)
            {
                keyArray[i-found] = keyArray[i];
            }
        }
        currentSize -= found;
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean isEmpty()
    {
        return currentSize == 0;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public int size()
    {
        return currentSize;
    }


}
