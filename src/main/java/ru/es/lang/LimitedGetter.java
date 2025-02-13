package ru.es.lang;

/**
 * Created by saniller on 23.07.2015.
 */
public class LimitedGetter<T>
{
    ESGetter<T> getter;
    long limitMillis;

    public LimitedGetter(ESGetter<T> getter, long limitMillis)
    {
        this.getter = getter;
        this.limitMillis = limitMillis;
    }

    T lastValue;
    long lastUpdated = 0;

    public T get()
    {
        if (lastUpdated + limitMillis < System.currentTimeMillis())
        {
            lastUpdated = System.currentTimeMillis();
            lastValue = getter.get();
        }

        return lastValue;
    }

    public T getRefreshed()
    {
        lastUpdated = System.currentTimeMillis();
        lastValue = getter.get();
        return lastValue;
    }

    public void refresh()
    {
        lastUpdated = 0;
    }
}
