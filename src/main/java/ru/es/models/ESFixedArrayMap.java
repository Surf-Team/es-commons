package ru.es.models;



import java.util.Map;

public abstract class ESFixedArrayMap<K,V>
{
    int limit;
    public K[] keyArray;
    public V[] valueArray;
    int currentSize = 0;

    // в качестве KEY не использовать int, итд. Либо переопределять метод get чтобы было eqals
    
    public ESFixedArrayMap(int limit)
    {
        this.limit = limit;
        keyArray = createKeyArray(limit);
        valueArray = createValueArray(limit);
    }

    protected abstract K[] createKeyArray(int size);

    protected abstract V[] createValueArray(int size);


    
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

    //todo не протестировано!
    public void remove(K key)
    {
        if (containsKey(key))
        {
            int moveKey = 0;
            for (int i = 0; i < currentSize; i++)
            {
                if (keyArray[i] == key)
                {
                    moveKey--;
                }
                else
                {
                    keyArray[i + moveKey] = keyArray[i];
                    valueArray[i + moveKey] = valueArray[i];
                }
            }
            currentSize += moveKey; // move key < 0
        }
    }

    
    public void putUnchecked(K key, V value)
    {
        keyArray[currentSize] = key;
        valueArray[currentSize] = value;
        currentSize++;
    }

    
    public void clear()
    {
        for (int i = 0; i < currentSize; i++)
        {
            keyArray[i] = null;
            valueArray[i] = null;
        }
        currentSize = 0;
    }

    
    public boolean isEmpty()
    {
        return currentSize == 0;
    }

    
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

    
    public int size()
    {
        return currentSize;
    }

    
    public void putAll(Map<K, V> calculatedRoutings)
    {
        for (Map.Entry<K,V> entry : calculatedRoutings.entrySet())
        {
            put(entry.getKey(), entry.getValue());
        }
    }

    
    public boolean containsKey(K containsThis)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == containsThis)
                return true;
        }
        return false;
    }

    
    public boolean containsValue(V containsThis)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (valueArray[i] == containsThis)
                return true;
        }
        return false;
    }

    
    public V getValueInIndex(int i)
    {
        if (i > currentSize)
            throw new IndexOutOfBoundsException();

        return valueArray[i];
    }
}
