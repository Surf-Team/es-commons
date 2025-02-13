package ru.es.lang;

import ru.es.log.Log;

import java.util.HashMap;
import java.util.Map;

public class ESEventHandler<T>
{
    public Map<Integer, ESEvent<T>> listeners = new HashMap<>();

    public void addListener(ESEvent<T> t)
    {
        listeners.put(t.hashCode(), t);
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
        for (ESEvent<T> listener : listeners.values())
        {
            try
            {
                listener.event(o);
            }
            catch (Exception e)
            {
                Log.warning("Error with event: "+listener.getClass().getName());
                e.printStackTrace();
            }
        }
    }

}
