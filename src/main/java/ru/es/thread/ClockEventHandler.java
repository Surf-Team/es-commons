package ru.es.thread;

import ru.es.lang.ESSetter;
import javolution.util.FastTable;

import java.util.List;

public class ClockEventHandler
{
    private int cycle = 0;
    private List<ESSetter<Integer>> clock40Tasks = new FastTable<>();

    public void event()
    {
        cycle++;
        try
        {
            for (ESSetter<Integer> s : clock40Tasks)
            {
                try
                {
                    s.set(cycle);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void addOnClock40(ESSetter<Integer> runnable)
    {
        clock40Tasks.add(runnable);
    }
}
