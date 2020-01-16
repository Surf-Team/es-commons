package ru.es.lang;

/**
 * Created by saniller on 09.07.2017.
 */
public interface Call<T>
{
    T call(T oldValue);
}
