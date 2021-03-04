package ru.es.util;

import javolution.util.FastTable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ESQueue<E> implements Iterable<E>
{
    public int limit = Integer.MAX_VALUE;

    public ESQueue(int queueLimit)
    {
        limit = queueLimit;
    }

    public final List<E> list = new FastTable<>();


    public void add(E t)
    {
        if (list.size() >= limit)
        {
            while (list.size() >= limit)
            {
                list.remove(list.size()-1);
            }
        }
        list.add(0, t);
    }

    public void addAll(Collection<E> t)
    {
        list.addAll(t);
    }

    public E get(int index)
    {
        if (list.size() > index)
            return list.get(index);
        return null;
    }

    @Override
    public Iterator<E> iterator()
    {
        return list.iterator();
    }

    @Override
    public void forEach(Consumer<? super E> action)
    {
        list.forEach(action);
    }

    @Override
    public Spliterator<E> spliterator()
    {
        return list.spliterator();
    }

    public int size()
    {
        return list.size();
    }

    public void removeAll(Collection<E> collection)
    {
        list.removeAll(collection);
    }

    public void clear()
    {
        list.clear();
    }

    public boolean isEmpty()
    {
        return list.isEmpty();
    }
}
