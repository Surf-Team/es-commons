package ru.es.lang;

import java.util.HashMap;
import java.util.Map;

public class ESThrowingEventHandler<T>
{
    public Map<Integer, ESThrowingEvent<T>> listeners = new HashMap<>();

    public void addListener(ESThrowingEvent<T> t)
    {
        listeners.put(t.hashCode(), t);
    }

    public void removeListener(ESThrowingEvent<T> t)
    {
        listeners.remove(t);
    }

    public void event(T o) throws Exception
    {
        for (ESThrowingEvent<T> listener : listeners.values())
        {
            listener.event(o);
        }
    }

}
