package ru.es.annotation;

import org.jdom2.Element;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class AnnotatedXMLWriter
{
	public static<T> Element listToXml(List<T> list, Class<T> tClass, String childNames) throws IllegalAccessException
	{
		Element ret = new Element("root");
		for (T t : list)
		{
			Element objectElement = new Element(childNames);
			ret.addContent(objectElement);

			for (Field f : tClass.getFields())
			{
				if (Modifier.isStatic(f.getModifiers()))
					continue;

				XmlParseSettings parseSettings = f.getAnnotation(XmlParseSettings.class);
				if (parseSettings != null)
				{
					if (!parseSettings.allowParse())
						continue;
				}

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
		if (o == null)
			return "@[null]";
		return o.toString();
	}
}
