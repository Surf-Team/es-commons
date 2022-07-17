package ru.es.annotation.xml;

import org.jdom2.Attribute;
import org.jdom2.Element;
import ru.es.log.Log;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class AnnotatedXML
{
	public static<T> List<T> getList(Class<T> objectsType, Element rootElement) throws Exception
	{
		List<T> ret = new ArrayList<>();
		for (Element e : rootElement.getChildren())
		{
			T object = objectsType.getConstructor().newInstance();
			parse(object, e);
			ret.add(object);
		}

		return ret;
	}


	public static<T> void parse(T object, Element e) throws IllegalAccessException
	{
		Class<T> objectClass = (Class<T>) object.getClass();

		for (Field f : objectClass.getDeclaredFields())
		{
			f.setAccessible(true);

			String fieldName = f.getName();

			Log.warning("Parsing field: "+fieldName);

			for (Attribute a : e.getAttributes())
				Log.warning("exist attrs: "+a.getName());
			Attribute attribute = e.getAttribute(fieldName);
			if (attribute != null)
			{
				f.set(object, parseValue(f.getType(), attribute.getValue()));
			}
			else
			{
				Element element = e.getChild(fieldName);
				Class fieldType = f.getType();
				if (fieldType.isArray())
				{
					var objectType = fieldType.getComponentType();
					int arraySize = element.getAttributes().size();
					Object[] array = (Object[]) Array.newInstance(objectType, arraySize);
					if (arraySize > 0)
					{
						int i = 0;
						for (Attribute a : element.getAttributes())
						{
							array[i] = parseValue(objectType, a.getValue());
							i++;
						}
					}
					f.set(object, array);
				}
				else
				{
					throw new RuntimeException("Поле '"+fieldName+"' не найдено в XML или оно не является примитивным объектом или массивом примитивных объектов.");
				}
			}
		}
	}

	private static Object parseValue(Class<?> type, String value)
	{
		if (type == Boolean.class)
			return Boolean.parseBoolean(value);
		else if (type == String.class)
			return value;
		else if (type == int.class)
			return Integer.parseInt(value);
		else if (type == long.class)
			return Long.parseLong(value);
		else if (type == float.class)
			return Float.parseFloat(value);
		else if (type == double.class)
			return Double.parseDouble(value);
		else
			throw new RuntimeException("Unknown type: "+type.getName()+" = "+value);
	}

}
