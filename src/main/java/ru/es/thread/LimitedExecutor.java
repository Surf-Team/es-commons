package ru.es.thread;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LimitedExecutor
{
    private final ConcurrentHashMap<Object, Runnable> objectLimitedTasks3000 = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object, Runnable> objectLimitedTasks200 = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object, Runnable> objectLimitedTasks40 = new ConcurrentHashMap<>();

    final List<Object> toRemove200 = new ArrayList<>();
    final List<Object> toRemove3000 = new ArrayList<>();
    final List<Object> toRemove40 = new ArrayList<>();

    public LimitedExecutor(TimeEventHandler timeEventHandler)
    {
        timeEventHandler.addListener(200, new Runnable() {
            @Override
            
            public void run()
            {
                toRemove200.clear();
                for (Map.Entry<Object, Runnable> entry : objectLimitedTasks200.entrySet())
                {
                    toRemove200.add(entry.getKey());
                    entry.getValue().run();
                }

                for (Object o : toRemove200)
                {
                    objectLimitedTasks200.remove(o);
                }
            }
        });

        timeEventHandler.addListener(3000, new Runnable()
        {
            
            public void run()
            {
                toRemove3000.clear();
                for (Map.Entry<Object, Runnable> entry : objectLimitedTasks3000.entrySet())
                {
                    toRemove3000.add(entry.getKey());
                    entry.getValue().run();
                }

                for (Object o : toRemove3000)
                {
                    objectLimitedTasks3000.remove(o);
                }
            }
        });


        timeEventHandler.addListener(40, new Runnable()
        {
            @Override
            
            public void run()
            {
                if (!objectLimitedTasks40.isEmpty())
                {
                    toRemove40.clear();

                    for (Map.Entry<Object, Runnable> entry : objectLimitedTasks40.entrySet())
                    {
                        toRemove40.add(entry.getKey());
                        entry.getValue().run();
                    }

                    for (int i = 0; i < toRemove40.size(); i++)
                    {
                        objectLimitedTasks40.remove(toRemove40.get(i));
                    }
                }
            }
        });
    }

    public void addObjectLimitedTask3000(Object component, Runnable r)
    {
        objectLimitedTasks3000.put(component, r);
    }

    public void addObjectLimitedTask200(Object component, Runnable r)
    {
        objectLimitedTasks200.put(component, r);
    }

    public void addObjectLimitedTask40(Object component, Runnable r)
    {
        objectLimitedTasks40.put(component, r);
    }

}
