package ru.es.models;

public abstract class ESFixedArraySet<K>
{
    int limit;
    public K[] keyArray;
    int currentSize = 0;

    public ESFixedArraySet(int limit)
    {
        changeSize(limit);
    }

    protected abstract K[] createKeyArray(int size);

    public void add(K key)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == key)
            {
                return;
            }
        }

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

    public void changeSize(int size)
    {
        if (keyArray != null)
            clear();

        this.limit = size;
        keyArray = createKeyArray(size);
    }

    // делаем get вместо этого метода
    /*public boolean contains(K key)
    {
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i] == key)
            {
                return true;
            }
        }

        return false;
    } */
}
