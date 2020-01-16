package ru.es.lang;

/**
 * Created by saniller on 07.05.2017.
 */
public interface Converter<S, T>
{
    T convert(S src) throws Exception;
}
