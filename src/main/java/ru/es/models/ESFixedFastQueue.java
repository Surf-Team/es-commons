package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;

public class ESFixedFastQueue<K>
{
    public static void main(String[] args)
    {
        ESFixedFastQueue<String> fixedFastQueue = new ESFixedFastQueue<String>(new String[10]);

        Log.warning("Init: ");
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Add: 1");
        fixedFastQueue.add("1");
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Add: 2");
        fixedFastQueue.add("2");
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Add: 3");
        fixedFastQueue.add("3");
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Remove first");
        fixedFastQueue.removeFirst();
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Remove first");
        fixedFastQueue.removeFirst();
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Add: 4");
        fixedFastQueue.add("4");
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Clear");
        fixedFastQueue.clear();
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }


        Log.warning("Add: 1");
        fixedFastQueue.add("1");
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Add: 2");
        fixedFastQueue.add("2");
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Add: 3");
        fixedFastQueue.add("3");
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

        Log.warning("Remove first");
        fixedFastQueue.removeFirst();
        for (int i = 0; i < fixedFastQueue.size; i++)
        {
            Log.warning(i+": "+fixedFastQueue.get(i));
        }

    }

    K[] array;
    int offset = 0;
    int limit = 0;

    public int size = 0;

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public ESFixedFastQueue(K[] emptyArray)
    {
        this.array = emptyArray;
        limit = emptyArray.length;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void add(K k)
    {
        array[(size + offset) % limit] = k;
        size++;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public K get(int index)
    {
        return array[(index + offset) % limit];
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void clear()
    {
        size = 0;
        offset = 0;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void removeFirst()
    {
        offset++;
        size--;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public int size()
    {
        return size;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public int getLimit()
    {
        return limit;
    }
}
