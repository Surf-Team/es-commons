package ru.es.annotation;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// что позволяет DependencyManager:
// 1. Создавать объекты XmlCollection лишь указав класс объекта и url на xml файл
// 2. Новые объекты коллекций записываются в коллекцию с коллекциями :) В итоге любую коллекцию можно достать по указанию класса
// 3. Доставать конкретные объекты внутри коллекций по ключу, указав класс объекта и ключ
// 		ключ указывается при обьявлении поля через @UniqueKey
//		у объекта может быть 1 или несколько ключей
public class DependencyManager
{
	private Map<Class, XmlCollection> collections = new HashMap<>();

	// создать XmlCollection из файла и добавить в список существующих коллекций
	public<T> XmlCollection<T> addCollection(Class<T> c, URL xmlFile) throws Exception
	{
		XmlCollection<T> xmlCollection = new XmlCollection<T>(c, xmlFile);
		collections.put(c, xmlCollection);
		return xmlCollection;
	}

	// добавить XmlCollection в список коллекций
	public<T> void addCollection(Class<T> c, XmlCollection<T> collection)
	{
		collections.put(c, collection);
	}

	// достать коллекцию по указанному классу
	public<T> XmlCollection<T> getCollection(Class<T> tClass)
	{
		return collections.get(tClass);
	}


	// Доставать объект из коллекции по ключу
	// ключ указывается при обьявлении поля через @UniqueKey
	public<T> T getValue(Class<T> tClass, Object key)
	{
		return (T) getCollection(tClass).get(key);
	}


}
