package ru.es.jfx.binding;

import ru.es.lang.ESGetter;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Created by saniller on 16.07.2016.
 */
public class ESDynamicGetter<T> extends ESProperty<T>
{
    // при изменении любых Property, перечисленных в getWhenTheyChange значение этой Property меняется автоматически на getter.get()
    // имеет одно ограничение: вызывать set вручную нельзя. set вызывается только внутренними функциями

    public boolean blockNotifyIfNull = false; // запрет обновления листенеров, если установлено значение null.
    // Помогает избежать кучи проблем, когда параметр удалён, или ещё не создан
    ESGetter<T> getter;

    public ESDynamicGetter(ESGetter<T> getter, Observable... getWhenTheyChange)
    {
        super(getter.get());
        this.getter = getter;

        InvalidationListener chl = new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                setOnChange(getter.get());
            }
        };

        for (Observable p : getWhenTheyChange)
        {
            p.addListener(chl);
        }

        chl.invalidated(null);
    }

    public ESDynamicGetter(ESGetter<T> getter)
    {
        super(getter.get());
        this.getter = getter;

        ESChangeListener chl = new ESChangeListener(true)
        {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue)
            {
                setOnChange(getter.get());
            }
        };
    }

    // только для тех случаев, когда нельзя сразу добавить getWhenTheyChange. Это можно сделать позже через addOnChange
    /**public ESDynamicGetter(T def)
    {
        super(def);
        setOnChange(def);
    }  **/

    public ESDynamicGetter(ESGetter<T> getter, ESChangeListener<T> onSet, Property... getWhenTheyChange)
    {
        this(getter, getWhenTheyChange);
        this.getter = getter;
        if (onSet != null)
            addListener(onSet);
    }

    public ESDynamicGetter(ESGetter<T> getter, ESChangeListener<T> onSet, ObservableList... getWhenTheyChange)
    {
        this(getter, getWhenTheyChange);
        this.getter = getter;
        if (onSet != null)
            addListener(onSet);
    }

    public ESDynamicGetter(ESGetter<T> getter, ObservableList... getWhenTheyChange)
    {
        super(null);
        setOnChange(getter.get());
        this.getter = getter;

        InvalidationListener listener = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable)
            {
                setOnChange(getter.get());
            }
        };

        for (ObservableList p : getWhenTheyChange)
        {
            p.addListener(listener);
        }
    }

    public ESDynamicGetter(ESGetter<T> getter, ObservableSet... getWhenTheyChange)
    {
        super(null);
        setOnChange(getter.get());
        this.getter = getter;

        InvalidationListener chl = new InvalidationListener() {
            @Override
            public void invalidated(Observable observable)
            {
                setOnChange(getter.get());
            }
        };

        for (ObservableSet p : getWhenTheyChange)
        {
            p.addListener(chl);
        }
    }

    public void addListenerFor(Observable... observables)
    {
        InvalidationListener listener = new InvalidationListener()
        {
            @Override
            public void invalidated(Observable observable)
            {
                setOnChange(getter.get());
            }
        };

        for (Observable p : observables)
        {
            p.addListener(listener);
        }

        setOnChange(getter.get());
    }

    private void setOnChange(T newValue)
    {
        if (newValue == null && blockNotifyIfNull)
            return;

        setValue(newValue);
    }
}
