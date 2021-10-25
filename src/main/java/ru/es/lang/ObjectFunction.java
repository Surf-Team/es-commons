package ru.es.lang;

public interface ObjectFunction<T>
{
	void invoke(T object, String command);
}