package ru.es.models;

import ru.es.jfx.ESXmlObject;

import java.util.WeakHashMap;

/**
 * Created by saniller on 19.04.2017.
 */
public class IdObjectManager<T extends IdObject> extends ESXmlObject
{
    public WeakHashMap<T, Object> objects = new WeakHashMap<>();

    public T getObjectById(long id)
    {
        for (T d : objects.keySet())
        {
            if (d.idProperty().get() == id)
                return d;
        }
        return null;
    }

    public void add(T obj)
    {
        objects.put(obj, new Object());
    }

    public void remove(T obj)
    {
        objects.remove(obj);
    }

    @Override
    public String getXmlName()
    {
        return null;
    }
}
