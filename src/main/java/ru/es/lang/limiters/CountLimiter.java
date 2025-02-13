package ru.es.lang.limiters;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class CountLimiter
{
    Map<String, Set<String>> accountsMap = new HashMap<>();
    public int limit;

    public CountLimiter(int limit)
    {
        this.limit = limit;
    }

    public synchronized boolean allow(String ip, String account)
    {
        Set<String> thisIpLastLogins = accountsMap.computeIfAbsent(ip, new Function<String, Set<String>>() {
            @Override
            public Set<String> apply(String s)
            {
                return new HashSet<String>();
            }
        });

        thisIpLastLogins.add(account);
        if (thisIpLastLogins.size() > limit)
            return false;

        return true;
    }
}
