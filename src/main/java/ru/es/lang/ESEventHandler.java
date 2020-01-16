package ru.es.lang;

import java.util.ArrayList;
import java.util.List;

public class ESEventHandler<T>
{
    public List<ESEvent<T>> listeners = new ArrayList<>();

    public void addListener(ESEvent<T> t)
    {
        listeners.add(t);
    }

    public void removeListener(ESEvent<T> t)
    {
        listeners.remove(t);
    }

    public void clearListeners()
    {
        listeners.clear();
    }

    public void event(T o)
    {
        for (ESEvent<T> listener : listeners)
        {
            listener.event(o);
        }
    }

}
