package ru.es.jfx.events;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Created by saniller on 04.05.2017.
 */
public abstract class ESEventHandler<T extends Event> implements EventHandler<T>
{
    T lastEvent;
    T lastDragEvent;
    @Override
    public final void handle(T event)
    {
        lastEvent = event;
        if (event.getEventType() == MouseEvent.MOUSE_DRAGGED)
            lastDragEvent = event;

        handleImpl(event);
    }

    public abstract void handleImpl(T event);

    public void repeatLastDragEvent()
    {
        handleImpl(lastDragEvent);
    }
}
