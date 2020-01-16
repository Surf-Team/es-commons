package ru.es.lang;

public interface Bridge<S, T>
{
    T get(S from);
}

