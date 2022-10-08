package ru.es.surfFramework;

import ru.es.log.Log;
import ru.es.surfFramework.annotations.Config;
import ru.es.surfFramework.annotations.VariableUntilShutdown;
import ru.es.surfFramework.models.MethodProcessor;
import ru.es.util.XmlConfig;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class SurfFrameworkProcessor
{
	private final XmlConfig xmlConfig;

	public SurfFrameworkProcessor(XmlConfig xmlConfig)
	{
		this.xmlConfig = xmlConfig;
	}

	private final Map<Class<? extends Annotation>, MethodProcessor> methodProcessors = new HashMap<>();

	public <T> void process(T newInstance) throws IllegalAccessException
	{
		process(newInstance, (Class<T>) newInstance.getClass());
	}

	public <T> void process(T newInstance, Class<T> c) throws IllegalAccessException
	{
		// parse fields
		for (Field f : c.getFields())
		{
			for (Annotation a : f.getAnnotations())
			{
				// parse config fields
				if (a.annotationType() == Config.class)
				{
					Config config = (Config) a;

					if (f.getType() == Integer.class)
						f.set(newInstance, xmlConfig.getInt(config.name()));
					else if (f.getType() == Float.class)
						f.set(newInstance, xmlConfig.getFloat(config.name()));
					else if (f.getType() == Double.class)
						f.set(newInstance, xmlConfig.getDouble(config.name()));
					else if (f.getType() == Long.class)
						f.set(newInstance, xmlConfig.getLong(config.name()));
					else if (f.getType() == Boolean.class)
						f.set(newInstance, xmlConfig.getBoolean(config.name()));
					else if (f.getType() == String.class)
						f.set(newInstance, xmlConfig.getValue(config.name()));
				}
			}
		}

		// parse methods
		for (Method m : c.getMethods())
		{
			for (Annotation annotationClass : m.getAnnotations())
			{
				var processor = methodProcessors.get(annotationClass.annotationType());
				if (processor != null)
					processor.process(m, newInstance, annotationClass);
			}
		}
	}

	public void registerMethodAnnotation(Class<? extends Annotation> annotationType, MethodProcessor methodProcessor)
	{
		methodProcessors.put(annotationType, methodProcessor);
	}

	void transferFields(Object oldObject, Object newObject)
	{
		if (oldObject == null)
			return;
		
		for (Field newField : newObject.getClass().getFields())
		{
			for (Annotation a : newField.getAnnotations())
			{
				if (a.annotationType() == VariableUntilShutdown.class)
				{
					VariableUntilShutdown info = (VariableUntilShutdown) a;

					Field oldField = null;
					try
					{
						oldField = oldObject.getClass().getField(newField.getName());
					}
					catch (NoSuchFieldException e)
					{
						e.printStackTrace();
						Log.warning("Cant transfer field: "+newField.getName()+", "+newObject.getClass().getSimpleName());
						continue;
					}

					Object transferValue = null;
					try
					{
						transferValue = oldField.get(oldObject);
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Log.warning("Cant find transfer value: "+newField.getName()+", "+newObject.getClass().getSimpleName());
						continue;
					}

					try
					{
						newField.set(newObject, transferValue);
						Log.warning("" +
								"" +
								"Success set transfered value to new object: " + newField.getName() + "=" + transferValue + ", " + newObject.getClass().getSimpleName());
					}
					catch (Exception e)
					{
						e.printStackTrace();
						Log.warning("Cant set transfered value to new object: " + newField.getName() + "=" + transferValue + ", " + newObject.getClass().getSimpleName());
						continue;
					}
				}
			}
		}
	}
}
