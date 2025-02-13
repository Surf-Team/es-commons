package ru.es.yaml;

import org.yaml.snakeyaml.Yaml;
import ru.es.util.ESProperties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class YamlElement
{
	private final Yaml yaml = new Yaml();

	YamlElement parent;

	// yaml может хранить:
	// атрибуты (ключ, значение)
	private ESProperties attributes = new ESProperties();
	private LinkedHashMap<String, String> sortedAttributes = new LinkedHashMap<>();
	// потомков (ключ, йамл элемент)
	private Map<String, YamlElement> children = new LinkedHashMap<>();
	// списки из йамл элементов
	private List<YamlElement> list = new ArrayList<>();
	// просто значение
	private String value;

	public YamlElement(File file) throws IOException
	{
		InputStream stream = file.toURI().toURL().openStream();
		Object linkedHashMap = yaml.load(stream);
		stream.close();

		parse(linkedHashMap);
	}


	public YamlElement(String text) throws IOException
	{
		LinkedHashMap<String, Object> linkedHashMap = yaml.load(text);

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
				else if (value instanceof Boolean)
				{
					attributes.put(s, value.toString());
					sortedAttributes.put(s, value.toString());
				}
				else if (value instanceof Integer)
				{
					attributes.put(s, value.toString());
					sortedAttributes.put(s, value.toString());
				}
				else if (value instanceof Long)
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
			throw new RuntimeException("Yaml parser: unknown object type: "+container.getClass()+", value: "+container);
	}

	public ESProperties getProperties()
	{
		return attributes;
	}

	public void setProperty(String name, String value)
	{
		attributes.setProperty(name, value);
		sortedAttributes.put(name, value);
	}

	public List<String> getStringList(String forEach)
	{
		YamlElement ret = children.get(forEach);
		List<String> list = new ArrayList<>();

		if (ret != null)
		{
			for (YamlElement child : ret.list)
			{
				list.add(child.value);
			}
		}

		return list;
	}

	public YamlElement getChild(String forEach)
	{
		return children.get(forEach);
	}

	public List<YamlElement> getList()
	{
		return list;
	}
}
