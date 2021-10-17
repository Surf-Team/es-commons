package ru.es.lang.limiters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TimeLimiter
{
    Map<String, Long> keys = new ConcurrentHashMap<>();
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
        long lastAuth = keys.getOrDefault(key, 0L);

        if (System.currentTimeMillis() - lastAuth < delay)
        {
            return false;
        }
        else
        {
            keys.put(key, System.currentTimeMillis());
            return true;
        }
    }

    public void clean()
    {
        keys.clear();
    }
}
