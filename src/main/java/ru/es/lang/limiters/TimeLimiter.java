package ru.es.lang.limiters;

import ru.es.lang.ESValue;
import ru.es.lang.ESValueDefault;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimeLimiter
{
    private final Map<String, ESValue<Long>> keys = new ConcurrentHashMap<>();
    long defaultDelay;
    public boolean runSynchronized = true;
    private final Object sync = new Object();

    public TimeLimiter(long defaultDelay)
    {
        this.defaultDelay = defaultDelay;
    }

    public TimeLimiter(boolean runSynchronized)
    {
        this.runSynchronized = runSynchronized;
    }

    public boolean allow(String key)
    {
        if (runSynchronized)
        {
            synchronized (sync)
            {
                return allowImpl(key, defaultDelay);
            }
        }
        else
            return allowImpl(key, defaultDelay);
    }

    public boolean allow(String key, long delay)
    {
        if (runSynchronized)
        {
            synchronized (sync)
            {
                return allowImpl(key, delay);
            }
        }
        else
            return allowImpl(key, delay);
    }

    private boolean allowImpl(String key, long delay)
    {
        ESValue<Long> lastEvent = keys.get(key);
        if (lastEvent == null)
        {
            lastEvent = new ESValueDefault<>(0L);
            keys.put(key, lastEvent);
        }

        if (lastEvent.get() + delay > System.currentTimeMillis())
        {
            return false;
        }
        else
        {
            lastEvent.set(System.currentTimeMillis());
            return true;
        }
    }

    public void clean()
    {
        keys.clear();
    }
}
