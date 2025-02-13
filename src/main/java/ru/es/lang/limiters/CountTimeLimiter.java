package ru.es.lang.limiters;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

// удобный класс для создания ограничений на вызов каких либо функций
public class CountTimeLimiter
{
    Map<String, List<Long>> map = new ConcurrentHashMap<>();

    long time;
    int countLimit;

    // "не более чем countLimit в течение time"
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

    // возвращает false, если пользователь привысил лимит + учитывает эту попытку
    public synchronized boolean allow(String user)
    {
        List<Long> lastWrongLogins = map.computeIfAbsent(user, createMap);

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

    // возвращает false, если пользователь привысил лимит + учитывает эту попытку
    public synchronized boolean allow(String user, boolean strong)
    {
        List<Long> lastWrongLogins = map.computeIfAbsent(user, createMap);

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

        if (!strong)
            return count <= countLimit;
        else
            return count < countLimit;
    }

    // лояльный вариант, который не добавляет новые элементы, когда лимит превышен
    public synchronized boolean allowAndAdd(String user)
    {
        if (allow(user))
        {
            add(user);
            return true;
        }
        return false;
    }

    // лояльный вариант, который не добавляет новые элементы, когда лимит превышен
    public synchronized boolean allowAndAdd(String user, boolean strong)
    {
        if (allow(user, strong))
        {
            add(user);
            return true;
        }
        return false;
    }

    // более строгий вариант, учитывающий попытки даже тогда, когда лимит превышен
    public synchronized boolean addAndAllow(String user)
    {
        add(user);
        return allow(user);
    }

    public synchronized boolean allowUnique()
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

    public synchronized void add(String user)
    {
        List<Long> lastWrongLogins = null;

        for (var e : map.entrySet())
        {
            if (e.getKey().equals(user))
            {
                lastWrongLogins = e.getValue();
                break;
            }
        }

        if (lastWrongLogins == null)
        {
            lastWrongLogins = new LinkedList<>();
            map.put(user, lastWrongLogins);
        }

        lastWrongLogins.add(System.currentTimeMillis());
    }
    
}
