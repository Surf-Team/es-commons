package ru.es.surfFramework;

import ru.es.lang.Templated;
import ru.es.log.Log;

import java.util.*;

public class SurfFramework
{
	public final Collection<Class<?>> classes;
	public final Map<Class<?>, SurfObjectCollection<?>> objectManagers = new LinkedHashMap<>();
	protected final Map<Class<?>, Object> singletons = new HashMap<>();
	protected final SurfFrameworkProcessor processor;
	public final InvokeManager invokeManager = new InvokeManager();
	private final Map<String, Class<?>> classByName = new HashMap<>();

	public SurfFramework(Collection<Class<?>> classes)
	{
		this.classes = classes;
		this.processor = new SurfFrameworkProcessor();


		for (Class<?> c : classes)
		{
			classByName.put(c.getName(), c);
		}
	}

	public void start() throws Exception
	{

		for (var collection : objectManagers.values())
			collection.process();


		// в singletons так же включены все последователи от @Collect
		for (var object : singletons.values())
		{
			processor.processAfter(object, object.getClass(), this);
		}
	}

	public <T> T getSingleton(Class<T> sClass)
	{
		return (T) singletons.get(sClass);
	}

	// метод для получения объектов, которые наследуют класс с аннотацией @Collect
	public<T> List<T> getObjects(Class<T> c)
	{
		try
		{
			List<T> ret = (List<T>) objectManagers.get(c).objects;
			return ret;
		}
		catch (Exception e)
		{
			Log.warning("Object manager not found for class: "+c.getName());
			e.printStackTrace();
			return null;
		}
	}

	Collection<Class<?>> getAllClasses()
	{
		return classes;
	}


	public<Impl extends Templated> Impl getImplement(Class<Impl> implClass, Object templateObject)
	{
		for (Impl implObject : getObjects(implClass))
		{
			if (implObject.getTemplateClass() == templateObject.getClass())
				return implObject;
		}
		return null;
	}

	public<Impl extends Templated> Impl getImplementByClass(Class<Impl> implClass, Class<?> templateClass)
	{
		for (Impl implObject : getObjects(implClass))
		{
			if (implObject.getTemplateClass() == templateClass)
				return implObject;
		}
		return null;
	}

	public SurfFrameworkProcessor getProcessor()
	{
		return processor;
	}

	public Class<?> getClass(String name)
	{
		return classByName.get(name);
	}

	public<T, K extends T> void add(Class<T> collectionClass, boolean processClass, K object)
	{
		getObjects(collectionClass).add(object);

		if (processClass)
		{
			singletons.put(object.getClass(), object);
			try
			{
				processor.process(object);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public<T> void registerObjectManager(Class<T> tClass)
	{
		SurfObjectCollection<T> collection = new SurfObjectCollection<>(tClass, this);
		objectManagers.put(tClass, collection);
	}
}
