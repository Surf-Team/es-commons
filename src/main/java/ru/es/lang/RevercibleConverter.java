package ru.es.lang;

/**
 * Created by saniller on 10.07.2017.
 */
public interface RevercibleConverter<S, T>
{
    T convertA(S src);
    S convertB(T src);
}
