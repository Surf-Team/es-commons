package ru.es.lang.limiters;

import java.util.HashMap;
import java.util.Map;

public class TimeLimiter
{
    Map<String, Long> lastAuthMap = new HashMap<>();
    long limitMillis;

    public TimeLimiter(long limitMillis)
    {
        this.limitMillis = limitMillis;
    }

    public synchronized boolean allow(String ip)
    {
        long lastAuth = lastAuthMap.getOrDefault(ip, 0L);

        if (System.currentTimeMillis() - lastAuth < limitMillis)
        {
            return false;
        }
        else
        {
            lastAuthMap.put(ip, System.currentTimeMillis());
            return true;
        }
    }

}
