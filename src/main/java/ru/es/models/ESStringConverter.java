package ru.es.models;

public interface ESStringConverter<T>
{
	String toString(T t);

	T fromString(String text);
}
