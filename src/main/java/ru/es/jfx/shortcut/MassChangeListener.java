package ru.es.jfx.shortcut;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

/**
 * Created by saniller on 22.07.2016.
 */
@Deprecated // use ESFXUtils.massChangeListener instead
public abstract class MassChangeListener
{
    public MassChangeListener(Observable... onChange)
    {
        InvalidationListener listener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable)
            {
                MassChangeListener.this.changed();
            }
        };

        for (Observable l : onChange)
        {
            l.addListener(listener);
        }
        changed();
    }


    public abstract void changed();
}
