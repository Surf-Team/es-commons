package ru.es.jfx.eventDispatchers;

import javafx.application.Platform;
import javolution.util.FastTable;
import javolution.util.FastSet;

import java.util.List;
import java.util.Set;

public class AppProcessEventHandler
{
    private boolean loaded = false;

    private List<Runnable> executeOnCLose = new FastTable<>();
    private Set<Runnable> executeOnLoadedSet = new FastSet<>();
    private List<Runnable> executeOnLoaded = new FastTable<>();

    public AppProcessEventHandler()
    {

    }

    public void executeSafe(Runnable r)
    {
        if (loaded)
        {
            Platform.runLater(r);
        }
        else
            executeOnLoaded.add(r);
    }



    public void executeSafeOnce(Runnable r)
    {
        if (loaded)
        {
            Platform.runLater(r);
        }
        else
            executeOnLoadedSet.add(r);
    }

    public void executeOnClose(Runnable r)
    {
        executeOnCLose.add(r);
    }


    public void eventClosed()
    {
        for (Runnable r : executeOnCLose)
        {
            if (r != null)
                r.run();
        }
    }

    public void eventLoaded()
    {
        loaded = true;

        for (Runnable r : executeOnLoaded)
        {
            Platform.runLater(r);
        }
        executeOnLoaded.clear();

        for (Runnable r : executeOnLoadedSet)
        {
            Platform.runLater(r);
        }
        executeOnLoadedSet.clear();
    }


    public boolean isLoaded()
    {
        return loaded;
    }
}
