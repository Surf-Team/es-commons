package ru.es.util;

import ru.es.lang.ESGetter;

import java.util.HashMap;
import java.util.Map;

public class ESProperties
{
	private final Map<String, String> strings = new HashMap<>();
	private final Map<String, Boolean> booleans = new HashMap<>();
	private final Map<String, Integer> ints = new HashMap<>();
	private final Map<String, Double> doubles = new HashMap<>();
	private final Map<String, Float> floats = new HashMap<>();
	private final Map<String, Object> objects = new HashMap<>();

	public float getFloat(String name, float defaultVal)
	{
		Float ret = floats.get(name);
		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public void setFloat(String name, float val)
	{
		floats.put(name, val);
	}


	public int getInt(String name, int defaultVal)
	{
		Integer ret = ints.get(name);
		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public void setInt(String name, int val)
	{
		ints.put(name, val);
	}


	public String getString(String name, String defaultVal)
	{
		String ret = strings.get(name);
		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public void setString(String name, String val)
	{
		strings.put(name, val);
	}

	public double getDouble(String name, double defaultVal)
	{
		Double ret = doubles.get(name);

		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public void setDouble(String name, double val)
	{
		doubles.put(name, val);
	}

	public boolean getBoolean(String name, boolean defaultVal)
	{
		Boolean ret = booleans.get(name);

		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public void setBoolean(String name, boolean val)
	{
		booleans.put(name, val);
	}

	public<T> T getObject(String name, ESGetter<T> defaultVal)
	{
		try
		{
			T ret = (T) objects.get(name);
			if (ret != null)
				return ret;
		}
		catch (Exception e) { }

		T ret = defaultVal.get();
		objects.put(name, ret);
		return ret;
	}

	public<T> void setObject(String name, T obj)
	{
		objects.put(name, obj);
	}
}
