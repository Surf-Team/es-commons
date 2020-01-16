package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;

import java.util.Map;

public abstract class ESFixedArrayMap<K,V>
{
    int limit;
    public K[] keyArray;
    public V[] valueArray;
    int currentSize = 0;

    // в качестве KEY не использовать int, итд. Либо переопределять метод get чтобы было eqals
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public ESFixedArrayMap(int limit)
    {
        this.limit = limit;
        keyArray = createKeyArray(limit);
        valueArray = createValueArray(limit);
    }

    protected abstract K[] createKeyArray(int size);

    protected abstract V[] createValueArray(int size);


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void put(K key, V value)
    {
        boolean contains = false;
        int index = -1;

        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == key)
            {
                contains = true;
                index = i;
                break;
            }
        }

        if (contains)
        {
            valueArray[index] = value;
        }
        else
        {
            keyArray[currentSize] = key;
            valueArray[currentSize] = value;
            currentSize++;
        }
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void putUnchecked(K key, V value)
    {
        keyArray[currentSize] = key;
        valueArray[currentSize] = value;
        currentSize++;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void clear()
    {
        for (int i = 0; i < currentSize; i++)
        {
            keyArray[i] = null;
            valueArray[i] = null;
        }
        currentSize = 0;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean isEmpty()
    {
        return currentSize == 0;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public V get(K key)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == key)
            {
                return valueArray[i];
            }
        }

        return null;
    }

    // Избегать этого метода! лучше делать get
    /*public boolean containsKey(K key)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == key)
            {
                return true;
            }
        }
        return false;
    }       */

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public int size()
    {
        return currentSize;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void putAll(Map<K, V> calculatedRoutings)
    {
        for (Map.Entry<K,V> entry : calculatedRoutings.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean containsKey(K containsThis)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == containsThis)
                return true;
        }
        return false;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean containsValue(V containsThis)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (valueArray[i] == containsThis)
                return true;
        }
        return false;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public V getValueInIndex(int i)
    {
        if (i > currentSize)
            throw new IndexOutOfBoundsException();

        return valueArray[i];
    }
}
