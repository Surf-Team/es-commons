package ru.es.lang;


import java.util.LinkedList;
import java.util.List;

public interface ObjectMap<T>
{
	List<T> getObjects();

	// поиск по всем ключам @UniqueKey
	T get(Object key);

	void reload() throws Exception;

	void remove(T object);

	void add(T object) throws IllegalAccessException;

	boolean isSaveable();

	void save() throws Exception;

	Class<T> getTClass();

	default void clear()
	{
		throw new RuntimeException("Not done");
	}
}
