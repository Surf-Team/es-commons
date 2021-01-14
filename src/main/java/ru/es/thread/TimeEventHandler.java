package ru.es.thread;

import javolution.util.FastTable;
import ru.es.lang.ESGetter;

import java.util.List;

public class TimeEventHandler
{
    List<Task> tasks = new FastTable<>();

    public void run()
    {
        try
        {
            long currentTime = System.currentTimeMillis();
            for (int i = 0; i < tasks.size(); i++)
            {
                tasks.get(i).check(currentTime);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public class Task
    {
        long lastRun = System.currentTimeMillis();
        ESGetter<Long> interval;
        Runnable r;

        Task(ESGetter<Long> interval, Runnable r)
        {
            this.interval = interval;
            this.r = r;
        }

        void check(long currentTime)
        {
            if (lastRun + interval.get() < currentTime)
            {
                lastRun = System.currentTimeMillis();
                r.run();
            }
        }

        public void stop()
        {
            tasks.remove(this);
        }
    }

    public Task addListener(long interval, Runnable r)
    {
        Task t = new Task(()->interval, r);
        tasks.add(t);
        return t;
    }

    public void addListener(ESGetter<Long> interval, Runnable r)
    {
        tasks.add(new Task(interval, r));
    }
}
