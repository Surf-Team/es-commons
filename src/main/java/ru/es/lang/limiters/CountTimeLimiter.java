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

    private static final Function<String, List<Long>> createMap = new Function<String, List<Long>>() {
        @Override
        public List<Long> apply(String s)
        {
            return new LinkedList<>();
        }
    };

    // repeated
    public synchronized boolean allow(String ip)
    {
        List<Long> lastWrongLogins = map.computeIfAbsent(ip, createMap);

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

    // лояльный вариант, который не добавляет новые элементы, когда лимит привышен
    public synchronized boolean allowAndAdd(String ip)
    {
        if (allow(ip))
        {
            add(ip);
            return true;
        }
        return false;
    }

    // более строгий вариант, учитывающий попытки даже тогда, когда лимит превышен
    public synchronized boolean addAndAllow(String ip)
    {
        add(ip);
        return allow(ip);
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
        List<Long> lastWrongLogins = map.computeIfAbsent(ip, createMap);

        lastWrongLogins.add(System.currentTimeMillis());
    }
    
}
