package ru.es.jfx.application;

import java.util.HashMap;
import java.util.Map;

public class AUserStatus
{
    public Map<String, Integer> values = new HashMap<>();

    public void increment(String key)
    {
        int val = values.getOrDefault(key, 0);
        values.put(key, val+1);
    }
}
