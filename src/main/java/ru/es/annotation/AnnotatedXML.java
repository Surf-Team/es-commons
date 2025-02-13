package ru.es.annotation;

import org.jdom2.Attribute;
import org.jdom2.Element;
import ru.es.lang.VariableProvider;
import ru.es.log.Log;

import java.lang.reflect.*;
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

	// спарсить объект
	public static<T> void parse(T object, Class<? extends T> objectClass, Element e, boolean allowSuperclass) throws Exception
	{
		parse(object, objectClass, e, null, allowSuperclass);
	}

	// спарсить объект с учётом DependencyManager
	public static<T> void parse(T object, Class<? extends T> objectClass, Element e,
								DependencyManager dependencyManager, boolean allowSuperclass)
			throws Exception
	{
		parse(object, objectClass, e, dependencyManager, allowSuperclass, new VariableProvider());
	}

	// спарсить объект с учётом VariableProvider
	public static<T> void parse(T object, Class<? extends T> objectClass, Element e,
								DependencyManager dependencyManager,
								boolean allowSuperclass,
								VariableProvider variableProvider)
			throws Exception
	{
	    AnnotatedParserSettings settings = new AnnotatedParserSettings();
		settings.dependencyManager = dependencyManager;
		settings.allowParseSuperclass = allowSuperclass;
		settings.variableProvider = variableProvider;
		parse(object, objectClass, e, settings);
	}

	// спарсить объект с учётом VariableProvider
	public static<T> void parse(T object, Class<? extends T> objectClass, Element e, AnnotatedParserSettings settings)
			throws Exception
	{
		for (Field f : settings.allowParseSuperclass ? objectClass.getFields() : objectClass.getDeclaredFields())
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
					f.set(object, AnnotatedObject.parseValue(f.getType(),
							settings.variableProvider.getVariableString(attribute.getValue()),
							settings.dependencyManager, f));
				}
				catch (NumberFormatException nfe)
				{
					Log.warning("Error in field: "+fieldName);
					throw nfe;
				}
				catch (NullPointerException nfe)
				{
					Log.warning("Error in field: "+fieldName+", variableProvider: "+settings.variableProvider+", attribute: "+attribute);
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


					/*for (Element child2 : child.getChildren())
					{
						for (Attribute a : child2.getAttributes())
						{
							if (a.getName().equalsIgnoreCase(f.getName()))
							{
								subAttribute = a;
								break;
							}
						}
						if (subAttribute != null)
							break;
					} */
				}

				if (subAttribute != null && settings.allowParseSubElement)
				{
					try
					{
						f.set(object, AnnotatedObject.parseValue(f.getType(),
								settings.variableProvider.getVariableString(subAttribute.getValue()),
								settings.dependencyManager, f));
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
									Array.set(array, i, AnnotatedObject.parseValue(objectType,
											element.getAttributeValue(arrayInfo.elementNames()[i]), settings.dependencyManager, f));
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
						f.set(object, settings.variableProvider.getVariableString(text));
					}
					else
					{
						throw new RuntimeException("Поле '" + fieldName + "' не найдено в XML для класса "+objectClass.getSimpleName()+" или оно не является примитивным объектом или массивом примитивных объектов.");
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


}
