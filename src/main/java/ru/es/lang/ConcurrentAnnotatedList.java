package ru.es.lang;

import ru.es.annotation.AnnotatedUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentAnnotatedList<T> implements ObjectMap<T>
{
	public final Class<T> tClass;
	private Map<Object, T> map = new ConcurrentHashMap<>();
	private List<T> list = new CopyOnWriteArrayList<>();

	// можно делать авто-создание мапы по ключу с помощью аннотации Key
	public ConcurrentAnnotatedList(Class<T> tClass)
	{
		this.tClass = tClass;
	}

	// нельзя менять значения списка напрямую
	public List<T> getObjects()
	{
		return list;
	}

	// поиск по всем ключам @UniqueKey
	public T get(Object key)
	{
		return map.get(key);
	}


	@Override
	public void reload() throws Exception
	{
		throw new Exception("Cannot to reload simple list "+tClass.getSimpleName());
	}

	@Override
	public void remove(T object)
	{
		try
		{
			var key = AnnotatedUtils.getKey(object);
			list.remove(object);
			map.remove(key);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void add(T object)
	{
		try
		{
			var key = AnnotatedUtils.getKey(object);
			list.add(object);
			map.put(key, object);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	@Override
	public boolean isSaveable()
	{
		return false;
	}

	@Override
	public void save() throws Exception
	{
		throw new Exception("Cannot to save simple list "+tClass.getSimpleName());
	}

	@Override
	public Class<T> getTClass()
	{
		return tClass;
	}

	public int size()
	{
		return list.size();
	}

	public void clear()
	{
		map.clear();
		list.clear();
	}
}
