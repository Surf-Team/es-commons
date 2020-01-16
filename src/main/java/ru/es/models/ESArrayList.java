package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.annotation.Slow;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import ru.es.log.Log;

import java.util.*;

public class ESArrayList<K> implements List<K>, Observable
{
    public static void main(String[] args)
    {
        ESArrayList<Float> arr = new ESArrayList<Float>(10);

        for (int i = 0; i < 100; i++)
        {
            arr.add(0f+i);
            Log.warning("size: "+arr.size());
        }

        for (Float f: arr)
        {
            Log.warning("f: "+f);
        }

        ESArrayList<Integer> arr2 = new ESArrayList<Integer>(10);

        for (int i = 0; i < 100; i++)
        {
            arr2.add(i);
            Log.warning("size: "+arr2.size());
        }

        for (Integer f: arr2)
        {
            Log.warning("f: "+f);
        }

        for (int i = 0; i < 100; i++)
        {
            arr2.remove(i);
            Log.warning("size: "+arr2.size());
        }

        for (Integer f: arr2)
        {
            Log.warning("f: "+f);
        }
    }

    static IteratorsPool iteratorsPool = new IteratorsPool();


    static class IteratorsPool extends ReusablePool<Itr>
    {
        public IteratorsPool()
        {
            super("ESArrayListIterators", false, Integer.MAX_VALUE, 10);
        }

        @Override
        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        protected Itr createNew()
        {
            return new Itr();
        }

        @Override
        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        public void clean(Itr itr)
        {

        }

        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        public Itr getIterator(ESArrayList list)
        {
            Itr ret = getClean();
            ret.beginUse(list);
            return ret;
        }
    }


    private int limit;
    public K[] keyArray;
    private int currentSize = 0;
    private int initSize;

    final Object sync = new Object();

    private transient int modCount = 0;


    public ESArrayList(int initSize)
    {
        this.initSize = initSize;
        keyArray = createArray(initSize);
        this.limit = initSize;
    }

    public ESArrayList()
    {
        this(4);
    }

    //protected abstract K[] createArray(int size);
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    protected K[] createArray(int size)
    {
        return (K[]) new Object[size];
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean add(K key)
    {
        synchronized (sync)
        {
            if (currentSize == limit)
            {
                this.limit *= 2;
                K[] newKeyArray = createArray(this.limit);
                for (int i = 0; i < keyArray.length; i++)
                {
                    newKeyArray[i] = keyArray[i];
                }
                keyArray = newKeyArray;
            }

            keyArray[currentSize] = key;

            currentSize++;
            modCount++;
        }
        invalidated();
        return true;
    }

    @Slow
    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean remove(Object key)
    {
        if (key instanceof Number)
        {
            synchronized (sync)
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
                        keyArray[i - found] = keyArray[i];
                    }
                }
                currentSize -= found;
                modCount++;
            }
        }
        else
        {
            synchronized (sync)
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
                        keyArray[i - found] = keyArray[i];
                    }
                }
                currentSize -= found;
                modCount++;
            }
        }
        invalidated();
        return currentSize > 0;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean containsAll(Collection<?> c)
    {
        try
        {
            throw new Exception("Not supported");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean addAll(Collection<? extends K> c)
    {
        /*for (K k : c)
        {
            add(k);
        } */

        synchronized (sync)
        {
            for (K key : c)
            {
                if (currentSize == limit)
                {
                    this.limit *= 2;
                    K[] newKeyArray = createArray(this.limit);
                    for (int i = 0; i < keyArray.length; i++)
                    {
                        newKeyArray[i] = keyArray[i];
                    }
                    keyArray = newKeyArray;
                }

                keyArray[currentSize] = key;

                currentSize++;
            }
            modCount++;
        }
        invalidated();

        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends K> c)
    {
        throw new RuntimeException("Not implemented");
    }

    @Slow
    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean removeAll(Collection<?> c)
    {
        synchronized (sync)
        {
            for (Object key : c)
            {
                if (key instanceof Number)
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
                            keyArray[i - found] = keyArray[i];
                        }
                    }
                    currentSize -= found;
                    modCount++;
                }
                else
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
                            keyArray[i - found] = keyArray[i];
                        }
                    }
                    currentSize -= found;
                    modCount++;
                }
            }
        }
        invalidated();
        return true;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean retainAll(Collection<?> c)
    {
        throw new RuntimeException("Not implemented");
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void clear()
    {
        synchronized (sync)
        {
            for (int i = 0; i < currentSize; i++)
            {
                keyArray[i] = null;
            }
            currentSize = 0;
            modCount++;
        }
        invalidated();
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean isEmpty()
    {
        return currentSize == 0;
    }


    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public Iterator iterator()
    {
        return iteratorsPool.getIterator(this);
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public Object[] toArray()
    {
        Object[] arr = new Object[currentSize];
        synchronized (sync)
        {
            for (int i = 0; i < currentSize; i++)
                arr[i] = get(i);
        }
        return arr;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public <T> T[] toArray(T[] a)
    {
        synchronized (sync)
        {
            for (int i = 0; i < currentSize; i++)
                a[i] = (T) get(i);
        }
        return a;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public int size()
    {
        return currentSize;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public K get(int index)
    {
        if (index >= currentSize)
            throw new IndexOutOfBoundsException("index: "+index+", currentSize: "+currentSize);

        return keyArray[index];
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public K getUnsafe(int index)
    {
        return keyArray[index];
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public K set(int index, K element)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void add(int index, K element)
    {
        if (index < 0 || index > currentSize)
            throw new IndexOutOfBoundsException();

        synchronized (sync)
        {
            if (index == currentSize)
                add(element);
            else
            {
                // add, then move
                add(element);

                // move all down except needed index
                for (int i = currentSize-2; i >= index; i--)  // start from pre last index (last is element)
                {
                    keyArray[i+1] = keyArray[i];
                }

                keyArray[index] = element;
            }
        }
        invalidated();
    }

    @Slow
    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public K remove(int index)
    {
        K ret = keyArray[index];
        remove(ret);
        return ret;
    }


    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public int indexOf(Object o)
    {
        if (o instanceof Number)
        {
            for (int i = 0; i < currentSize; i++)
            {
                if (keyArray[i].equals(o))
                    return i;
            }
        }
        else
        {
            for (int i = 0; i < currentSize; i++)
            {
                if (keyArray[i] == o)
                    return i;
            }
        }
        return -1;
    }


    @Override
    public int lastIndexOf(Object o)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ListIterator<K> listIterator()
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public ListIterator<K> listIterator(int index)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<K> subList(int fromIndex, int toIndex)
    {
        throw new RuntimeException("Not implemented");
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean contains(Object key)
    {
        if (key instanceof Number)
        {
            synchronized (sync)
            {
                for (int i = 0; i < currentSize; i++)
                {
                    if (keyArray[i].equals(key))
                    {
                        return true;
                    }
                }
            }
        }
        else
        {
            synchronized (sync)
            {
                for (int i = 0; i < currentSize; i++)
                {
                    if (keyArray[i] == key)
                    {
                        return true;
                    }
                }
            }
        }

        return false;
    }


    private static class Itr implements Iterator {
        /**
         * Index of element to be returned by subsequent call to next.
         */
        int cursor = 0;

        /**
         * Index of element returned by most recent call to next or
         * previous.  Reset to -1 if this element is deleted by a call
         * to remove.
         */
        int lastRet = -1;

        /**
         * The modCount value that the iterator believes that the backing
         * List should have.  If this expectation is violated, the iterator
         * has detected concurrent modification.
         */
        int expectedModCount;
        ESArrayList collection;


        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        public void beginUse(ESArrayList collection)
        {
            this.collection = collection;
            expectedModCount = collection.modCount;
            cursor = 0;
            lastRet = -1;
        }


        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        public boolean hasNext()
        {
            if (cursor != collection.size())
                return true;
            else
            {
                iteratorsPool.addFree(this);
                return false;
            }
        }

        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        public Object next() {
            checkForComodification();
            try {
                int i = cursor;
                Object next = collection.get(i);
                lastRet = i;
                cursor = i + 1;
                return next;
            } catch (IndexOutOfBoundsException e) {
                checkForComodification();
                iteratorsPool.addFree(this);
                throw new NoSuchElementException("cursor: "+cursor);
            }
        }

        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        public void remove() {
            if (lastRet < 0)
            {
                iteratorsPool.addFree(this);
                throw new IllegalStateException();
            }
            checkForComodification();

            try {
                collection.remove(lastRet);
                if (lastRet < cursor)
                    cursor--;
                lastRet = -1;
                expectedModCount = collection.modCount;
            } catch (IndexOutOfBoundsException e) {
                iteratorsPool.addFree(this);
                throw new ConcurrentModificationException();
            }
        }

        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
        final void checkForComodification() {
            if (collection.modCount != expectedModCount)
            {
                iteratorsPool.addFree(this);
                throw new ConcurrentModificationException();
            }
        }
    }

    List<InvalidationListener> listeners;

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addListener(InvalidationListener listener)
    {
        if (listeners == null)
            listeners = new ESArrayList<InvalidationListener>();

        listeners.add(listener);
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void removeListener(InvalidationListener listener)
    {
        if (listeners != null)
            listeners.remove(listener);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    private void invalidated()
    {
        if (listeners != null)
        {
            for (InvalidationListener l : listeners)
                l.invalidated(this);
        }
    }



}
