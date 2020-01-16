package ru.es.jfx.binding;

import ru.es.lang.ESGetter;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javolution.util.FastSet;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by saniller on 07.11.2016.
 */
public class ESDynamicSetWrapper<T> implements Observable
{
    // ВНИМАНИЕ!
    // во враппер засовывать ESSet, который при eqals всегда возвращает false

    ObjectProperty<ObservableSet<T>> currentShortcut = new SimpleObjectProperty<>(null);


    public ESDynamicSetWrapper(ESGetter<ObservableSet<T>> observableSetGetter, Observable... updateOnChange)
    {
        SetChangeListener<T> setChangeListener = new SetChangeListener<T>() {
            @Override
            public void onChanged(Change<? extends T> change)
            {
                setChanged();
            }
        };

        currentShortcut.addListener(new ChangeListener<ObservableSet<T>>()
        {
            @Override
            public void changed(ObservableValue<? extends ObservableSet<T>> observable, ObservableSet<T> oldValue, ObservableSet<T> newValue)
            {
                if (oldValue != null)
                    oldValue.removeListener(setChangeListener);
                if (newValue != null)
                    newValue.addListener(setChangeListener);

                setChanged();
            }
        });

        InvalidationListener chl = new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                currentShortcut.set(observableSetGetter.get());
                setChanged();
            }
        };

        for (Observable p : updateOnChange)
        {
            p.addListener(chl);
        }


        currentShortcut.set(observableSetGetter.get());
    }

    ObservableSet<T> dummySet = FXCollections.observableSet(new FastSet<T>());

    private void setChanged()
    {
        for (InvalidationListener l : listenersForAll)
        {
            l.invalidated(null);
        }
    }

    List<InvalidationListener> listenersForAll = new LinkedList<>();

    @Override
    public void addListener(InvalidationListener l)
    {
        listenersForAll.add(l);
    }

    public void removeListener(InvalidationListener l)
    {
        listenersForAll.remove(l);
    }

    public ObservableSet<T> get()
    {
        return currentShortcut.get();
    }
}
