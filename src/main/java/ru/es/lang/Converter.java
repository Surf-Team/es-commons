package ru.es.lang;

public interface Converter<S, T>
{
    T convert(S from);
}

