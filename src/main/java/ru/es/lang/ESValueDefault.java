package ru.es.lang;



/**
 * Created by saniller on 23.08.2015.
 */
public class ESValueDefault<T> implements ESValue<T>
{
    public ESValueDefault(T defaultValue)
    {
        value = defaultValue;
    }

    public T value;

    @Override
    
    public T get()
    {
        return value;
    }

    
    @Override
    public void set(T newValue)
    {
        value = newValue;
    }
}
