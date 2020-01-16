package ru.es.jfx.binding;

import ru.es.lang.ESGetter;

/**
 * Created by saniller on 09.01.2017.
 */
public abstract class ESPropertyUnlistened<T> extends ESProperty<T> implements ESGetter<T>
{
    public ESPropertyUnlistened()
    {
        super();
    }

    public abstract T get();

    public T getValue()
    {
        return get();
    }

    public abstract void set(T val);

    public void setValue(T val)
    {
        set(val);
    }
}
