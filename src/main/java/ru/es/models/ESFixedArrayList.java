package ru.es.models;



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

    
    public void add(K key)
    {
        keyArray[currentSize] = key;
        currentSize++;
    }

    
    public void clear()
    {
        for (int i = 0; i < currentSize; i++)
        {
            keyArray[i] = null;
        }
        currentSize = 0;
    }

    
    public boolean isEmpty()
    {
        return currentSize == 0;
    }

    
    public int size()
    {
        return currentSize;
    }

    
    public K get(int index)
    {
        return keyArray[index];
    }


    
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
