package ru.es.annotation;

import org.jdom2.Element;
import ru.es.lang.ObjectMap;
import ru.es.util.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// создание типичной коллекции с объектами прямо из XML файла.
// достаточно разметить (или можно даже не размечать) класс объектов
// !!! подробности в AnnotatedXML
// класс позволяет:
// 1) получать списки объектов
// 2) получать конкретный обхект по ключу. Ключ указывается через @UniqueKey
public class AnnotatedXmlRepository<T> extends XmlRepository implements ObjectMap<T>
{
	private List<T> objects;
	private Map<String, Map<?, T>> maps = new HashMap<>();

	private final Class<T> tClass;
	private Element rootXml;

	// можно делать авто-создание мапы по ключу с помощью аннотации Key
	public AnnotatedXmlRepository(Class<T> tClass, URL file) throws Exception
	{
		super(file, false);
		this.tClass = tClass;
		reload();
	}



	@Override
	public void remove(T object)
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void add(T object) throws IllegalAccessException
	{
		throw new RuntimeException("Not implemented");
	}

	@Override
	public boolean isSaveable()
	{
		return true;
	}

	@Override
	protected void reloadImpl(Element rootXml) throws Exception
	{
		this.rootXml = rootXml;
		// create list
		List<T> objects = AnnotatedXML.getList(tClass, rootXml);

		// create maps by Key annotation
		Map<String, Map<?, T>> maps = AnnotatedMap.createMaps(objects);

		this.objects = objects;
		this.maps = maps;
	}

	@Override
	public void save() throws Exception
	{
		rootXml.detach();
		FileUtils.saveXmlDocWideFormat(rootXml, new File(file.getFile()));
	}

	@Override
	public Class<T> getTClass()
	{
		return tClass;
	}

	public List<T> getObjects()
	{
		return objects;
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
}
