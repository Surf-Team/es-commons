package ru.es.jfx.graph;

import java.util.HashMap;
import java.util.Map;

public class GraphLinesContainer
{
    public Map<String, GraphLine> graphs = new HashMap<>();
    public final String name;

    public GraphLinesContainer(String name) {this.name = name;}

    public GraphLine addGraph(String name)
    {
        GraphLine line = new GraphLine(name);
        graphs.put(name, line);
        return line;
    }


    public GraphLine getLine(String lineName)
    {
        GraphLine line = graphs.get(lineName);
        if (line == null)
        {
            line = addGraph(lineName);
        }
        return line;
    }
}
