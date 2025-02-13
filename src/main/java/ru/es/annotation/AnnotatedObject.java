package ru.es.annotation;

import ru.es.lang.ObjectMap;
import ru.es.lang.ESStringConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class AnnotatedObject
{
	// создаёт объект, назначает поля, взятые из мапы
	// задаёт ID через UniqueKey
	public static<T> T createObject(Class<T> tClass, int id, Map<String, String> variables) throws Exception
	{
		T newObject = tClass.getConstructor().newInstance();
		boolean hasUniqueKey = false;

		for (Field f : tClass.getFields())
		{
			for (Annotation a : f.getAnnotations())
			{
				if (a.annotationType() == UniqueKey.class)
				{
					f.set(newObject, id);
					hasUniqueKey = true;
				}
			}
		}

		if (!hasUniqueKey)
			throw new Exception("Class "+tClass.getSimpleName()+" doesn't have @UniqueKey annotation");

		fillObject(newObject, variables);

		return newObject;
	}

	// назначает поля объекты, взятые из мапы variables
	public static<T> void fillObject(T object, Map<String, String> variables) throws Exception
	{
		for (Field f : object.getClass().getFields())
		{
			String fieldValue = variables.get(f.getName());

			if (fieldValue == null)
				continue; // это нормально - например когда ещё не сохранено значение добавленного поля

			f.set(object, parseValue(f.getType(), fieldValue, null, f));
		}
	}

	public static Object parseValue(Class type, String value, DependencyManager dependencyManager, Field f) throws NoSuchFieldException, IllegalAccessException
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
			ToString toString = f != null ? f.getAnnotation(ToString.class) : null;
			if (toString != null)
			{
				if (toString.value() == ESStringConverter.class) // is default converter
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
					Field singletonConverter = toString.value().getField("singleton");
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
				ObjectMap collection = dependencyManager.getCollection(type);

				if (collection == null)
					throw new RuntimeException("Unknown type: " + type.getName() + " = " + value + ", Xml collection not found.");

				return collection.get(value);
			}

			throw new RuntimeException("Cannot parse value: "+value+", "+type.getSimpleName());
		}
	}
}
