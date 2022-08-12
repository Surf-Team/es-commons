package ru.es.yaml;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class YamlElement
{
	private static final Yaml yaml = new Yaml();

	YamlElement parent;

	// yaml может хранить:
	// атрибуты (ключ, значение)
	private Properties attributes = new Properties();
	private LinkedHashMap<String, String> sortedAttributes = new LinkedHashMap<>();
	// потомков (ключ, йамл элемент)
	private Map<String, YamlElement> children = new LinkedHashMap<>();
	// списки из йамл элементов
	private List<YamlElement> list = new ArrayList<>();
	// просто значение
	private String value;

	public YamlElement(File file) throws IOException
	{
		Object linkedHashMap = yaml.load(file.toURI().toURL().openStream());

		parse(linkedHashMap);
	}

	private YamlElement(YamlElement parent)
	{
		this.parent = parent;
	}

	public YamlElement()
	{

	}

	private void parse(Object container)
	{
		if (container instanceof LinkedHashMap)
		{
			LinkedHashMap<String, Object> containerMap = (LinkedHashMap<String, Object>) container;
			for (String s : containerMap.keySet())
			{
				Object value = containerMap.get(s);

				if (value == null)
				{
					
				}
				else if (value instanceof String)
				{
					attributes.put(s, value.toString());
					sortedAttributes.put(s, value.toString());
				}
				else if (value instanceof LinkedHashMap)
				{
					YamlElement element = new YamlElement(this);
					element.parse(value);
					children.put(s, element);
				}
				else if (value instanceof List)
				{
					YamlElement element = new YamlElement(this);
					element.parse(value);
					children.put(s, element);
				}
				else
					throw new RuntimeException("Yaml parser: Unknown type: "+value.getClass());
			}
		}
		else if (container instanceof List)
		{
			List<Object> objects = (List<Object>) container;
			
			for (Object o : objects)
			{
				YamlElement element = new YamlElement(this);
				element.parse(o);
				list.add(element);
			}
		}
		else if (container instanceof String)
		{
			value = (String) container;
		}
		else
			throw new RuntimeException("Yaml parser: unknown object type: "+container.getClass().getName()+", value: "+container.toString());
	}

	public Properties getProperties()
	{
		return attributes;
	}

	public void setProperty(String name, String value)
	{
		attributes.setProperty(name, value);
		sortedAttributes.put(name, value);
	}


	private Object getYamlObject()
	{
		if (value != null)
		{
			return value;
		}
		else if (!list.isEmpty())
		{
			List<Object> ret = new ArrayList<>();
			for (YamlElement e : list)
			{
				ret.add(e.getYamlObject());
			}
			return ret;
		}
		else
		{
			LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
			for (String key : sortedAttributes.keySet())
			{
				map.put(key, sortedAttributes.get(key));
			}

			for (String key : children.keySet())
			{
				map.put(key, children.get(key).getYamlObject());
			}
			return map;
		}
	}

	public List<String> getStringList(String forEach)
	{
		YamlElement ret = children.get(forEach);
		List<String> list = new ArrayList<>();

		for (YamlElement child : ret.list)
		{
			list.add(child.value);
		}

		return list;
	}
}
