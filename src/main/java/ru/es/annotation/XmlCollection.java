package ru.es.annotation;

import org.jdom2.Element;
import ru.es.models.XmlRepository;
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
public class XmlCollection<T> extends XmlRepository
{
	private List<T> objects;
	private Map<String, Map<Object, T>> maps = new HashMap<>();

	private final Class<T> tClass;
	private Element rootXml;

	// можно делать авто-создание мапы по ключу с помощью аннотации Key
	public XmlCollection(Class<T> tClass, URL file) throws Exception
	{
		super(file, false);
		this.tClass = tClass;
		reload();
	}

	@Override
	protected void reloadImpl(Element rootXml) throws Exception
	{
		this.rootXml = rootXml;
		// create list
		List<T> objects = AnnotatedXML.getList(tClass, rootXml);

		// create maps by Key annotation
		Map<String, Map<Object, T>> maps = AnnotatedMap.createMaps(objects);

		this.objects = objects;
		this.maps = maps;
	}

	@Override
	public void save() throws Exception
	{
		rootXml.detach();
		FileUtils.saveXmlDocWideFormat(rootXml, new File(file.getFile()));
	}

	public List<T> getObjects()
	{
		return objects;
	}

	// поиск по всем ключам @UniqueKey
	public T get(Object key)
	{
		for (Map<Object, T> map : maps.values())
		{
			T ret = map.get(key);
			if (ret != null)
				return ret;
		}
		return null;
	}

	// поиск по конкретному ключу всем ключам
	public T get(Object key, String keyName)
	{
		return maps.get(keyName).get(key);
	}
}
