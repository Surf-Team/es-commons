package ru.es.lang;

/**
 * Created by saniller on 07.05.2017.
 */
public interface ESSetterThrowable<T>
{
    void set(T newValue) throws Exception;
}
