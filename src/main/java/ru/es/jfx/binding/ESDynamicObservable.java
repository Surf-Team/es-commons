package ru.es.jfx.binding;

import ru.es.lang.ESGetter;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.List;

public class ESDynamicObservable implements Observable
{
    private final ESProperty<Observable> currentObservable = new ESProperty<>();
    private final List<InvalidationListener> listeners = new ArrayList<>();

    public ESDynamicObservable(ESGetter<Observable> observableGetter, Observable... changeOn)
    {
        InvalidationListener inv1 = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable)
            {
                for (InvalidationListener l : listeners)
                    l.invalidated(null);
            }
        };

        currentObservable.addListener(new ChangeListener<Observable>() {
            @Override
            public void changed(ObservableValue<? extends Observable> observable, Observable oldValue, Observable newValue)
            {
                if (oldValue != null)
                    oldValue.removeListener(inv1);
                if (newValue != null)
                    newValue.addListener(inv1);

                inv1.invalidated(null);
            }
        });

        InvalidationListener listener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable)
            {
                currentObservable.set(observableGetter.get());
            }
        };
        for (Observable o : changeOn)
        {
            o.addListener(listener);
        }
        currentObservable.set(observableGetter.get());
    }

    @Override
    public void addListener(InvalidationListener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener)
    {
        listeners.remove(listener);
    }
}
