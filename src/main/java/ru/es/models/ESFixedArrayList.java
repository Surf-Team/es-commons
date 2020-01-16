package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;

public class ESFixedArrayList<K>
{
    int limit;
    public K[] keyArray;
    int currentSize = 0;

    public ESFixedArrayList(K[] initArray)
    {
        keyArray = initArray;
        this.limit = keyArray.length;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void add(K key)
    {
        keyArray[currentSize] = key;
        currentSize++;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void clear()
    {
        for (int i = 0; i < currentSize; i++)
        {
            keyArray[i] = null;
        }
        currentSize = 0;
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

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public K get(int index)
    {
        return keyArray[index];
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean contains(K key)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == key)
            {
                return true;
            }
        }

        return false;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void reset(K[] keyArray)
    {
        if (this.limit != keyArray.length)
        {
            this.keyArray = keyArray;
            this.limit = keyArray.length;
        }
        this.currentSize = 0;
    }

}
