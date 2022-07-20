package ru.es.annotation;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class DependencyManager
{
	private Map<Class, XmlCollection> collections = new HashMap<>();

	public<T> void addCollection(Class<T> c, XmlCollection<T> collection)
	{
		collections.put(c, collection);
	}

	public<T> void addCollection(Class<T> c, URL xmlFile) throws Exception
	{
		XmlCollection<T> xmlCollection = new XmlCollection<T>(c, xmlFile);
		collections.put(c, xmlCollection);
	}

	public<T> XmlCollection<T> getCollection(Class<T> tClass)
	{
		return collections.get(tClass);
	}



	public<T> T getValue(Class<T> tClass, String key)
	{
		return (T) getCollection(tClass).get(key);
	}


}
