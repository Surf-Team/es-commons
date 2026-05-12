package ru.es.lang;

public interface ESThrowingEvent<T>
{
    void event(T t) throws Exception;
}
