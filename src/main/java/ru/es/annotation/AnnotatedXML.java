package ru.es.annotation;

import org.jdom2.Attribute;
import org.jdom2.Element;
import ru.es.annotation.XmlParseSettings;
import ru.es.log.Log;

import javax.lang.model.type.ArrayType;
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
		parse(object, objectClass, e, null);
	}

	public static<T> void parse(T object, Class<? extends T> objectClass, Element e, DependencyManager dependencyManager) throws IllegalAccessException, NoSuchFieldException
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
					f.set(object, parseValue(f.getType(), attribute.getValue(), dependencyManager));
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
						f.set(object, parseValue(f.getType(), subAttribute.getValue(), dependencyManager));
					}
					catch (NumberFormatException nfe)
					{
						Log.warning("Error in field (2): "+fieldName);
						throw nfe;
					}
				}
				else
				{
					Group group = f.getAnnotation(Group.class);
					if (group != null)
						fieldName = group.name();

					Element element = e.getChild(fieldName);

					if (element == null && parseSettings != null && parseSettings.allowDefaultValue())
						continue;

					//Log.warning("Parsing array: "+fieldName);
					Class fieldType = f.getType();
					if (fieldType.isArray())
					{
						ArrayInfo arrayInfo = f.getAnnotation(ArrayInfo.class);
						
						if (arrayInfo == null)
							throw new RuntimeException("No array info for array field "+f.getName()+", "+objectClass.getName());

						var objectType = fieldType.getComponentType();
						int arraySize = arrayInfo.elementNames().length;

						Object array = Array.newInstance(objectType, arraySize);

						if (arraySize > 0)
						{
							for (int i = 0; i < arraySize; i++)
							{
								try
								{
									Array.set(array, i, parseValue(objectType,
											element.getAttributeValue(arrayInfo.elementNames()[i]), dependencyManager));
								}
								catch (NumberFormatException nfe)
								{
									Log.warning("Error in field (3): "+fieldName);
									throw nfe;
								}
							}
						}
						f.set(object, array);
					}
					else if (element != null)
					{
						String text = element.getText();
						f.set(object, text);
					}
					else
					{
						throw new RuntimeException("Поле '" + fieldName + "' не найдено в XML или оно не является примитивным объектом или массивом примитивных объектов.");
					}
				}
			}
		}
	}

	private static Object parseValue(Class type, String value, DependencyManager dependencyManager) throws NoSuchFieldException, IllegalAccessException
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
		{
			XmlCollection collection = dependencyManager.getCollection(type);
			if (collection == null)
				throw new RuntimeException("Unknown type: " + type.getName() + " = " + value+", Xml collection not found.");

			return collection.get(value);
		}
	}


	public static<T> Element listToXml(List<T> list, Class<T> tClass, String childNames) throws IllegalAccessException
	{
		Element ret = new Element("root");
		for (T t : list)
		{
			Element objectElement = new Element(childNames);
			ret.addContent(objectElement);

			for (Field f : tClass.getFields())
			{
				Group group = f.getAnnotation(Group.class);
				UniqueKey isKey = f.getAnnotation(UniqueKey.class);
				NoSave noSave = f.getAnnotation(NoSave.class);
				ArrayInfo arrayInfo = f.getAnnotation(ArrayInfo.class);

				if (tClass.isEnum() && tClass == f.getType())    // dont save enum values for enum object
					continue;

				if (noSave != null)
					continue;

				Element saveToElement = objectElement;
				if (group != null)
				{
					String elementName = group.name();
					saveToElement = getOrCreateElement(objectElement, elementName);
				}

				Object o = f.get(t);
				if (arrayInfo == null)
					saveToElement.setAttribute(f.getName(), toString(o));
				else
				{
					if (saveToElement == objectElement)
					{
						// field name == element name for arrays
						saveToElement = getOrCreateElement(objectElement, f.getName());
					}

					for (int i = 0; i < Array.getLength(o); i++)
					{
						saveToElement.setAttribute(arrayInfo.elementNames()[i], toString(Array.get(o, i)));
					}

				}

			}
		}
		return ret;
	}

	private static Element getOrCreateElement(Element root, String elementName)
	{
		Element subElement = root.getChild(elementName);
		if (subElement == null)
		{
			subElement = new Element(elementName);
			root.addContent(subElement);
		}
		return subElement;
	}

	private static String toString(Object o)
	{
		return o.toString();
	}
}
