package ru.es.jfx.graph;

import java.util.HashMap;
import java.util.Map;

public class GraphLine
{
    public final Map<Long, Double> timeStats = new HashMap<>();

    public final String name;

    public GraphLine(String name) {this.name = name;}

    public void increment(long t)
    {
        double newVal = timeStats.getOrDefault(t, 0.0) +1;
        timeStats.put(t, newVal);
    }
}
