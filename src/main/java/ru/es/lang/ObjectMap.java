package ru.es.lang;


import ru.es.util.ListUtils;

import java.util.ArrayList;
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

	default int getFreeID()
	{
		for (int i = 0; i < Integer.MAX_VALUE; i++)
		{
			var o = get(i);
			if (o == null)
				return i;
		}
		return -1;
	}
}
