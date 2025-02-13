package ru.es.lang;


import ru.es.annotation.UniqueKey;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class MultiKeyMap<T> implements ObjectMap<T>
{
	public final Class<T> tClass;
	public List<T> objectsRef;
	public boolean saveable  = false;
	private Map<String, Map<Object, T>> maps;
	private List<T> unmodificableList;

	// можно делать авто-создание мапы по ключу с помощью аннотации Key
	public MultiKeyMap(List<T> collection, Class<T> tClass)
	{
		this.tClass = tClass;
		recreate(collection);
	}

	public void recreate(List<T> collection)
	{
		List<T> unmodificableList = Collections.unmodifiableList(collection);
		Map<String, Map<Object, T>> maps = new LinkedHashMap<>();

		List<Field> keyFields = new ArrayList<>();
		for (Field f : tClass.getFields())
		{
			for (Annotation a : f.getAnnotations())
			{
				if (a instanceof UniqueKey)
				{
					keyFields.add(f);
					break;
				}
			}
		}
		List<Method> keyMethods = new ArrayList<>();
		for (Method m : tClass.getMethods())
		{
			for (Annotation a : m.getAnnotations())
			{
				if (a instanceof UniqueKey)
				{
					keyMethods.add(m);
					break;
				}
			}
		}
		for (var keyField : keyFields)
		{
			Map<Object, T> keyMap = new LinkedHashMap<>();
			maps.put(keyField.getName(), keyMap);

			for (T t : collection)
			{
				try
				{
					Object key = keyField.get(t);
					keyMap.put(key, t);
				}
				catch (IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		for (var keyMethod : keyMethods)
		{
			Map<Object, T> keyMap = new LinkedHashMap<>();
			maps.put(keyMethod.getName(), keyMap);

			for (T t : collection)
			{
				try
				{
					Object key = keyMethod.invoke(t);
					keyMap.put(key, t);
				}
				catch (IllegalAccessException | InvocationTargetException e)
				{
					throw new RuntimeException(e);
				}
			}
		}

		this.unmodificableList = unmodificableList;
		this.objectsRef = collection;
		this.maps = maps;
	}

	// нельзя менять значения списка напрямую
	public List<T> getObjects()
	{
		return unmodificableList;
	}

	// поиск по всем ключам @UniqueKey
	public T get(Object key)
	{
		for (Map<?, T> map : maps.values())
		{
			T ret = map.get(key);
			if (ret != null)
				return ret;
		}
		return null;
	}


	@Override
	public void reload() throws Exception
	{
		throw new Exception("Cannot to reload simple list "+tClass.getSimpleName());
	}

	@Override
	public void remove(T object)
	{
		objectsRef.remove(object);
		// мапы не пересчитываем, т.к. незачем
	}

	@Override
	public void add(T object) throws IllegalAccessException
	{
		objectsRef.add(object);

		// обновляем мапы для быстрого доступа к объектам по ключу
		List<Field> keyFields = new ArrayList<>();
		for (Field f : tClass.getFields())
		{
			for (Annotation a : f.getAnnotations())
			{
				if (a instanceof UniqueKey)
				{
					keyFields.add(f);
					break;
				}
			}
		}
		for (var keyField : keyFields)
		{
			Map<Object, T> keyMap = maps.get(keyField.getName());
			keyMap.put(keyField.get(object), object);
		}
	}

	public void addObject(Object object) throws IllegalAccessException
	{
		add((T) object);
	}

	@Override
	public boolean isSaveable()
	{
		return saveable;
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
		return objectsRef.size();
	}

	@Override
	public void clear()
	{
		recreate(new ArrayList<>());
	}
}
