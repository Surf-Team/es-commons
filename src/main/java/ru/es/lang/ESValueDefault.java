package ru.es.lang;

import com.allatori.annotations.ControlFlowObfuscation;

/**
 * Created by saniller on 23.08.2015.
 */
public class ESValueDefault<T> implements ESValue<T>
{
    public ESValueDefault(T defaultValue)
    {
        value = defaultValue;
    }

    private T value;

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public T get()
    {
        return value;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    @Override
    public void set(T newValue)
    {
        value = newValue;
    }
}
