package ru.es.services;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectManager
{
	public final Map<String, Object> objects = new ConcurrentHashMap<>();

	public<T> T get(String key)
	{
		return (T) objects.get(key);
	}

	public<T> T get(String key, T def)
	{
		var object = objects.get(key);
		if (object == null)
			return def;
		return (T) object;
	}

	public void set(String key, Object obj)
	{
		objects.put(key, obj);
	}
}
