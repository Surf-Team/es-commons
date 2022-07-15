package ru.es.annotation.map;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnotatedMap
{
	// можно использовать аннотацию Key чтобы сформировать мапу из списка
	public static <T> Map<Integer, T> createMap(List<T> items)
	{
		Map<Integer, T> ret = new HashMap<>();
		if (items.size() > 0)
		{
			Field keyField = null;
			Class c = items.get(0).getClass();
			for (Field f : c.getFields())
			{
				for (Annotation a : f.getAnnotations())
				{
					if (a instanceof UniqueKey)
					{
						keyField = f;
						break;
					}
				}
				if (keyField != null)
					break;
			}

			for (T t : items)
			{
				try
				{
					int key = (int) keyField.get(t);
					ret.put(key, t);
				}
				catch (IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		return ret;
	}

	// можно использовать аннотацию Key чтобы сформировать мапу из списка
	public static <K,V> Map<K, V> createMap(List<V> items, Field keyField)
	{
		Map<K, V> ret = new HashMap<>();
		if (items.size() > 0)
		{
			for (V item : items)
			{
				try
				{
					K key = (K) keyField.get(item);
					ret.put(key, item);
				}
				catch (IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		return ret;
	}

	// создаёт несколько карт по ключам, если ключей несколько
	public static <T> Map<String, Map<Object, T>> createMaps(List<T> items)
	{
		Map<String, Map<Object, T>> maps = new HashMap<>();
		if (items.size() > 0)
		{
			Class c = items.get(0).getClass();
			for (Field f : c.getFields())
			{
				for (Annotation a : f.getAnnotations())
				{
					if (a instanceof UniqueKey)
					{
						Map<Object, T> map = AnnotatedMap.createMap(items, f);
						maps.put(f.getName(), map);
						break;
					}
				}
			}
		}
		return maps;
	}
}
