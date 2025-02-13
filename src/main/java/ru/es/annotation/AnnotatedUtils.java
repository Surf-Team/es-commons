package ru.es.annotation;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AnnotatedUtils
{
	// получить значение у поля, помеченное как @UniqueKey
	public static Object getKey(Object annotatedObject) throws IllegalAccessException, InvocationTargetException
	{
		for (Field f : annotatedObject.getClass().getFields())
		{
			for (Annotation a : f.getAnnotations())
			{
				if (a instanceof UniqueKey)
				{
					return f.get(annotatedObject);
				}
			}
		}
		for (Method m : annotatedObject.getClass().getMethods())
		{
			for (Annotation a : m.getAnnotations())
			{
				if (a instanceof UniqueKey)
				{
					return m.invoke(annotatedObject);
				}
			}
		}
		throw new IllegalAccessException("UniqueKey annotation is not found in class "+annotatedObject.getClass().getSimpleName());
	}

	public static Field getKeyField(Class tClass) throws IllegalAccessException
	{
		for (Field f : tClass.getFields())
		{
			for (Annotation a : f.getAnnotations())
			{
				if (a instanceof UniqueKey)
				{
					return f;
				}
			}
		}

		throw new IllegalAccessException("UniqueKey annotation is not found in class "+tClass.getSimpleName());
	}

	// получить тип уникального значения
	public static Class<?> getKeyType(Class<?> tClass) throws IllegalAccessException
	{
		for (Field f : tClass.getFields())
		{
			for (Annotation a : f.getAnnotations())
			{
				if (a instanceof UniqueKey)
				{
					return f.getType();
				}
			}
		}

		throw new IllegalAccessException("UniqueKey annotation is not found in class "+tClass.getSimpleName());
	}
}
