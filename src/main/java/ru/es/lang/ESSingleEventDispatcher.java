package ru.es.lang;

import ru.es.thread.RunnableImpl;

public class ESSingleEventDispatcher implements Runnable
{
    public RunnableImpl onEvent;

    public void setOnEvent(RunnableImpl onEvent)
    {
        this.onEvent = onEvent;
    }

    public void event()
    {
        if (onEvent != null)
            onEvent.run();
    }

    public void run()
    {
        event();
    }
}
