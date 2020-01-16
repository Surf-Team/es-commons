package ru.es.lang.limiters;

import java.util.*;
import java.util.function.Function;

public class CountTimeLimiter
{
    Map<String, List<Long>> map = new HashMap<>();

    long time;
    int countLimit;

    public CountTimeLimiter(long time, int countLimit)
    {
        this.time = time;
        this.countLimit = countLimit;
    }

    // repeated
    public synchronized boolean allow(String ip)
    {
        List<Long> lastWrongLogins = map.computeIfAbsent(ip, new Function<String, List<Long>>() {
            @Override
            public List<Long> apply(String s)
            {
                return new LinkedList<>();
            }
        });

        long curTime = System.currentTimeMillis();

        int count = 0;
        if (!lastWrongLogins.isEmpty())
        {
            for (long l : lastWrongLogins)
            {
                if (l + time > curTime)
                    count++;
            }
        }

        return count <= countLimit;
    }

    public synchronized boolean allowUnique(String ip)
    {
        long curTime = System.currentTimeMillis();

        int count = 0;
        for (String s : map.keySet())
        {
            for (Long l : map.get(s))
            {
                if (l + time > curTime)
                {
                    count++;
                    break;
                }
            }
        }

        return count <= countLimit;
    }

    public synchronized void add(String ip)
    {
        List<Long> lastWrongLogins = map.computeIfAbsent(ip, new Function<String, List<Long>>() {
            @Override
            public List<Long> apply(String s)
            {
                return new LinkedList<>();
            }
        });

        lastWrongLogins.add(System.currentTimeMillis());
    }
    
}
