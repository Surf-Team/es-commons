package ru.es.util;

import org.jdom2.Attribute;
import org.jdom2.Element;
import ru.es.log.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class XMLUtils
{
	// читаем параметры из xml атрибутов, записываем их в класс
	public static void parse(Object o, Element e, boolean debug)
	{
		Log.warning("XmlUtils.parse: object: "+o.toString());
		for (Field f : o.getClass().getFields())
		{
			String fieldName = f.getName();
			//Log.warning("Checking field: "+fieldName+"...");
			Attribute a = e.getAttribute(fieldName);
			if (a != null)
			{
				try
				{
					if (f.getType() == int.class)
					{
						f.set(o, Integer.parseInt(a.getValue()));
						if (debug)
							Log.warning("XmlUtils.parse: set " + fieldName + "=" + f.get(o));
					}
					else if (f.getType() == boolean.class)
					{
						f.set(o, Boolean.parseBoolean(a.getValue()));
						if (debug)
							Log.warning("XmlUtils.parse: set " + fieldName + "=" + f.get(o));
					}
					else if (f.getType() == float.class)
					{
						f.set(o, Float.parseFloat(a.getValue()));
						if (debug)
							Log.warning("XmlUtils.parse: set " + fieldName + "=" + f.get(o));
					}
					else if (f.getType() == double.class)
					{
						f.set(o, Double.parseDouble(a.getValue()));
						if (debug)
							Log.warning("XmlUtils.parse: set " + fieldName + "=" + f.get(o));
					}
					else if (f.getType() == String.class)
					{
						f.set(o, a.getValue());
						if (debug)
							Log.warning("XmlUtils.parse: set " + fieldName + "=" + f.get(o));
					}
					else if (f.getType().isEnum())
					{
					/*
					var enumConstants = f.getType().getEnumConstants();
					boolean set = false;
					for (var constant : enumConstants)
					{
						if (constant.toString().equals(a.getValue()))
						{
							f.set(o, constant);
							set = true;
							if (debug)
								Log.warning("XmlUtils.parse: set " + fieldName + "=" + f.get(o));
						}
						break;
					}

					if (!set)
						throw new RuntimeException("Enum value not found: "+fieldName+"->"+a.getValue());
					*/
						try
						{
							Method valueOf = f.getType().getMethod("valueOf", String.class);
							Object value = valueOf.invoke(null, a.getValue());
							f.set(o, value);
						}
						catch (Exception exception)
						{
							throw new RuntimeException("Enum value not found: " + fieldName + "->" + a.getValue());
						}

					}
					else
						Log.warning("Attribute " + fieldName + ": unknown field type: " + f.getType());
				}
				catch (Exception ex)
				{
					throw new RuntimeException("Error parse xml attribute field: " + fieldName + "->" + a.getValue());
				}
			}
			else
				Log.warning("Attribute "+fieldName+" is null");
		}
	}
}
