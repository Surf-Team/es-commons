package ru.es.lang;

public interface ESStringConverter<T>
{
	String toString(T t);

	T fromString(String text);
}
