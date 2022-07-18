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
			parse(object, objectsType, e);
			ret.add(object);
		}

		return ret;
	}


	public static<T> void parse(T object, Class<? extends T> objectClass, Element e) throws IllegalAccessException, NoSuchFieldException
	{
		for (Field f : objectClass.getDeclaredFields())
		{
			XmlParseSettings parseSettings = f.getAnnotation(XmlParseSettings.class);
			if (parseSettings != null)
			{
				if (!parseSettings.allowParse())
					continue;
			}

			f.setAccessible(true);

			String fieldName = f.getName();

			//Log.warning("Parsing field: "+fieldName);

			//for (Attribute a : e.getAttributes())
				//Log.warning("exist attrs: "+a.getName());

			Attribute attribute = e.getAttribute(fieldName);
			if (attribute != null)
			{
				try
				{
					f.set(object, parseValue(f.getType(), attribute.getValue()));
				}
				catch (NumberFormatException nfe)
				{
					Log.warning("Error in field: "+fieldName);
					throw nfe;
				}
			}
			else
			{
				Attribute subAttribute = null;
				for (Element child : e.getChildren())
				{
					for (Attribute a : child.getAttributes())
					{
						if (a.getName().equalsIgnoreCase(f.getName()))
						{
							subAttribute = a;
							break;
						}
					}
					if (subAttribute != null)
						break;
				}

				if (subAttribute != null)
				{
					try
					{
						f.set(object, parseValue(f.getType(), subAttribute.getValue()));
					}
					catch (NumberFormatException nfe)
					{
						Log.warning("Error in field (2): "+fieldName);
						throw nfe;
					}
				}
				else
				{
					Element element = e.getChild(fieldName);

					if (element == null && parseSettings != null && parseSettings.allowDefaultValue())
						continue;

					Log.warning("Parsing array: "+fieldName);
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
								try
								{
									array[i] = parseValue(objectType, a.getValue());
								}
								catch (NumberFormatException nfe)
								{
									Log.warning("Error in field (3): "+fieldName);
									throw nfe;
								}

								i++;
							}
						}
						f.set(object, array);
					}
					else
					{
						if (parseSettings != null && parseSettings.allowDefaultValue())
							continue;
						else
							throw new RuntimeException("Поле '" + fieldName + "' не найдено в XML или оно не является примитивным объектом или массивом примитивных объектов.");
					}
				}
			}
		}
	}

	private static Object parseValue(Class type, String value) throws NoSuchFieldException, IllegalAccessException
	{
		if (type == boolean.class)
		{
			if (value.equals("1"))
				return true;
			else if (value.equals("0"))
				return false;
			else
				return Boolean.parseBoolean(value);
		}
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
		else if (type.isEnum())
		{
			// не тестировалось
			Object o = Enum.valueOf(type, value);
			if (o == null)
			{
				// find by ordinal
				Field valuesFiled = type.getField("values");
				Object[] values = (Object[]) valuesFiled.get(o);
				return values[(int) o];
			}
			else
				return o;

			//throw new RuntimeException("Enum value not found: "+type+", "+value);
		}
		else
			throw new RuntimeException("Unknown type: "+type.getName()+" = "+value);
	}

}
