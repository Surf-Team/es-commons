package ru.es.annotation;

import org.jdom2.Attribute;
import org.jdom2.Element;
import ru.es.log.Log;
import ru.es.models.ESStringConverter;

import java.lang.reflect.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// помогает парсить данные из XML
// по умолчанию, парсинг будет происходить для всех полей класса
// если поле не найдено, то будет ошибка
// чтобы определить уникальный ключ в коллекции (для дальнейшего поиска обхекта по ключу), нужно поставить аннотацию @UniqueKey
// чтобы не парсить какое-либо поле, нужно использовать аннотацию @XmlParseSettings(allowParse = false)
// если значение может отсутствовать (не обязательное), соответственно нужно разрешить иметь значение по умолчанию @XmlParseSettings(allowDefaultValue = true
// можно указать название группы через @Group(name = groupName), чтобы xml значение сохранялось внутри элемента с названием groupName
// @ArrayInfo позволит более детально сохранять и загружать массивы
// @NoSave позволит не сохранять поле
// @ToString сообщит о том что поле может быть преобразовано в строку и обратно
// использование DependencyManager позволит парсить ссылки на объекты
public class AnnotatedXML
{
	// получить список объектов из Xml файла (спарсить)
	// не использовать на прямую. Лучше использовать XmlCollection<T> getCollection
	public static<T> List<T> getList(Class<T> objectsType, Element rootElement) throws Exception
	{
		return getList(objectsType, rootElement.getChildren());
	}

	// создание типичных коллекций из xml файла
	public static<T> XmlCollection<T> getCollection(Class<T> objectsType, URL file) throws Exception
	{
		return new XmlCollection<T>(objectsType, file);
	}

	// спарсить объект
	public static<T> void parse(T object, Class<? extends T> objectClass, Element e, boolean allowSuperclass) throws Exception
	{
		parse(object, objectClass, e, null, allowSuperclass);
	}

	// спарсить объект с учётом DependencyManager
	public static<T> void parse(T object, Class<? extends T> objectClass, Element e, DependencyManager dependencyManager, boolean allowSuperclass)
			throws Exception
	{
		for (Field f : allowSuperclass ? objectClass.getFields() : objectClass.getDeclaredFields())
		{
			if (Modifier.isStatic(f.getModifiers()))
				continue;

			XmlParseSettings parseSettings = f.getAnnotation(XmlParseSettings.class);
			if (parseSettings != null)
			{
				if (!parseSettings.allowParse())
					continue;
			}

			f.setAccessible(true);

			String fieldName = f.getName();

			if (f.getName().equals("_element"))
			{
				f.set(object, e);
				continue;
			}

			//Log.warning("Parsing field: "+fieldName);

			//for (Attribute a : e.getAttributes())
				//Log.warning("exist attrs: "+a.getName());

			Attribute attribute = e.getAttribute(fieldName);
			ListSettings listSettings = f.getAnnotation(ListSettings.class);

			if (listSettings != null)
			{
				Element getChildrenFrom = e;
				if (!listSettings.subElement().isEmpty())
				{
					getChildrenFrom = e.getChild(listSettings.subElement());

					if (getChildrenFrom == null)
						throw new Exception("Sub element not found: "+listSettings.subElement());
				}

				List<Element> listElements = getChildrenFrom.getChildren(listSettings.elementsName());
				f.set(object, getList(listSettings.objectsClass(), listElements));
			}
			else if (attribute != null)
			{
				try
				{
					f.set(object, parseValue(f.getType(), attribute.getValue(), dependencyManager, f));
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
						f.set(object, parseValue(f.getType(), subAttribute.getValue(), dependencyManager, f));
					}
					catch (NumberFormatException nfe)
					{
						Log.warning("Error in field (2): "+fieldName);
						throw nfe;
					}
					catch (NullPointerException npe)
					{
						Log.warning("Error in field (3): "+fieldName);
						throw npe;
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
											element.getAttributeValue(arrayInfo.elementNames()[i]), dependencyManager, f));
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

	private static<T> List<T> getList(Class<T> objectsType, List<Element> elements) throws Exception
	{
		List<T> ret = new ArrayList<>();
		for (Element e : elements)
		{
			T object = objectsType.getConstructor().newInstance();
			parse(object, objectsType, e, true);
			ret.add(object);
		}

		return ret;
	}


	private static Object parseValue(Class type, String value, DependencyManager dependencyManager, Field f) throws NoSuchFieldException, IllegalAccessException
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
			ToString toString = f.getAnnotation(ToString.class);
			if (toString != null)
			{
				if (toString.stringConverter() == ESStringConverter.class) // is default converter
				{
					try
					{
						Constructor constructor = type.getConstructor(String.class);
						return constructor.newInstance(value);
					}
					catch (NoSuchMethodException e)
					{
						throw new RuntimeException("Cant create object from string: " + type.getName() + " = " + value + ". No (String) constructor!");
					}
					catch (InvocationTargetException e)
					{
						e.printStackTrace();
						throw new RuntimeException("Cant create object from string: " + type.getName() + " = " + value + ". " + e.getMessage());
					}
					catch (InstantiationException e)
					{
						throw new RuntimeException("Cant create object from string: " + type.getName() + " = " + value + ". " + e.getMessage());
					}
				}
				else
				{
					Field singletonConverter = toString.stringConverter().getField("singleton");
					if (singletonConverter == null)
					{
						throw new RuntimeException("ESStringConverter, указанный в "+f.getName()+", должен быть проинициализирован в виде статического публичного поля с названием singleton в том же классе.");
					}
					else
					{
						ESStringConverter converter = (ESStringConverter) singletonConverter.get(null);
						return converter.fromString(value);
					}
				}
			}

			if (dependencyManager != null)
			{
				XmlCollection collection = dependencyManager.getCollection(type);

				if (collection == null)
					throw new RuntimeException("Unknown type: " + type.getName() + " = " + value + ", Xml collection not found.");

				return collection.get(value);
			}

			throw new RuntimeException("Cannot parse value: "+value+", "+type.getSimpleName());
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
