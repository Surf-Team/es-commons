package ru.es.surfFramework;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SurfFrameworkProcessor
{
	public Set<Object> allObjects = new HashSet<>();

	public SurfFrameworkProcessor()
	{
	}

	private final Map<Class<? extends Annotation>, MethodProcessor> methodProcessors = new HashMap<>();
	private final Map<Class<? extends Annotation>, FieldProcessor> fieldProcessors = new HashMap<>();

	public <T> void process(T newInstance) throws IllegalAccessException
	{
		process(newInstance, (Class<T>) newInstance.getClass());
	}

	public <T> void process(T newInstance, Class<T> c) throws IllegalAccessException
	{
		allObjects.add(newInstance);

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

	public void processAfter(Object newInstance, Class<?> c, SurfFramework framework) throws Exception
	{
		allObjects.add(newInstance);

		for (Field f : c.getFields())
		{
			for (Annotation annotationClass : f.getAnnotations())
			{
				var processor = fieldProcessors.get(annotationClass.annotationType());
				if (processor != null)
					processor.process(f, newInstance, annotationClass, framework);
			}
		}
	}

	public void registerMethodAnnotation(Class<? extends Annotation> annotationType, MethodProcessor methodProcessor)
	{
		methodProcessors.put(annotationType, methodProcessor);
	}


	public void registerFieldAnnotation(Class<? extends Annotation> annotationType, FieldProcessor fieldProcessor)
	{
		fieldProcessors.put(annotationType, fieldProcessor);
	}


}
