package ru.es.models;



import ru.es.log.Log;

import java.util.ArrayList;
import java.util.Collection;

public class ESFixedQueueFastGet<K>
{
    public static void main(String[] args)
    {
        ESFixedQueueFastGet<Integer> collection = new ESFixedQueueFastGet<>(new Integer[10]);

        for (int i = 0; i < 30; i++)
        {
            collection.add(i);

            Log.warning("Size: " + collection.size());

            StringBuilder out = new StringBuilder();
            for (Integer o : collection.keyArray)
            {
                out.append(o);
                out.append(", ");
            }
            Log.warning(out.toString());
        }

        Log.warning("### REMOVE ###");

        for (int i = 30; i >= 0; i--)
        {
            collection.remove(i);
            Log.warning("removed "+i+", Size: " + collection.size());

            StringBuilder out = new StringBuilder();
            for (Integer o : collection.keyArray)
            {
                out.append(o);
                out.append(", ");
            }
            Log.warning(out.toString());
        }
    }

    int limit;
    public K[] keyArray;   // можно обращаться напрямую
    int currentSize = 0;

    public ESFixedQueueFastGet(K[] initArray)
    {
        this.keyArray = initArray;
        this.limit = initArray.length;
    }

    // добавляет в начало очереди, смещая последующие (медленно)
    
    public void add(K key)
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
    
    public void remove(K key)
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

    // удаляет все элементы, которые попадутся с таким ключём
    
    public void removeEquals(K key)
    {
        int found = 0;
        for (int i = 0; i < currentSize; i++)
        {
            if (keyArray[i].equals(key))
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

    
    public Collection<K> makeCollection()
    {
        Collection<K> collection = new ArrayList<>(currentSize);
        for (int i = 0; i < currentSize; i++)
            collection.add(keyArray[i]);

        return collection;
    }

    // set all, but dont create new array
    
    public void setAll(Collection<K> collection)
    {
        int i = 0;
        currentSize = 0;
        for (K k : collection)
        {
            keyArray[i] = k;
            i++;
            currentSize++;
            if (i >= limit)
                break;
        }
        //currentSize = ESMath.min(limit, collection.size());
    }

}
