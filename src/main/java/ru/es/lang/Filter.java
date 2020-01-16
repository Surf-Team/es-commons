package ru.es.lang;

/**
 * Created by saniller on 02.12.2016.
 */
public interface Filter<T>
{
    public boolean accept(T t);
}
