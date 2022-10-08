package ru.es.surfFramework;

import ru.es.lang.Invoker;
import ru.es.log.Log;
import ru.es.surfFramework.annotations.Collect;
import ru.es.surfFramework.annotations.Singleton;

import java.lang.annotation.Annotation;
import java.util.*;

public class SurfFramework
{
	protected final Collection<Class<?>> classes;
	protected final Map<Class<?>, SurfObjectCollection<?>> objectManagers = new HashMap<>();
	protected final Map<Class<?>, Object> singletons = new HashMap<>();
	private final Map<Object, Map<Class<?>, Object>> extendMap = new HashMap<>();
	private final Map<String, Object> globalObjectMap;
	final SurfFrameworkProcessor processor;

	public SurfFramework(Collection<Class<?>> classes, SurfFrameworkProcessor processor, Map<String, Object> globalObjectMap)
	{
		this.classes = classes;
		this.processor = processor;
		this.globalObjectMap = globalObjectMap;
		processClasses(classes);
	}

	protected void processClasses(Collection<Class<?>> classes)
	{
		// сканируем классы, создаём инстансы, делаем синглтоны или коллекции
		for (var c : classes)
		{
			for (Annotation a : c.getAnnotations())
			{
				if (a.annotationType() == Singleton.class)
				{
					// синглетоны
					try
					{
						Log.warning("SurfFramework: Create singleton: "+c.getSimpleName());
						Object newInstance = c.getConstructor().newInstance();
						singletons.put(c, newInstance);
						processor.process(newInstance);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else if (a.annotationType() == Collect.class)
				{
					// коллекции
					try
					{
						Log.warning("SurfFramework: Create object collection of items: "+c.getSimpleName());
						// регистрация интерфейсов, наследники которых будут создаваться автоматически и добавляться в коллекции
						objectManagers.put(c, new SurfObjectCollection<>(c, this));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
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

	public synchronized <T> T getExtend(Object whatToExtend, Class<T> villageVarsClass)
	{
		Map<Class<?>, Object> extendMap = this.extendMap.get(whatToExtend);
		if (extendMap == null)
		{
			extendMap = new HashMap<>();
			this.extendMap.put(whatToExtend, extendMap);
		}

		Object extend = extendMap.get(villageVarsClass);

		if (extend == null)
		{
			try
			{
				extend = villageVarsClass.getConstructor(whatToExtend.getClass()).newInstance(whatToExtend);
			}
			catch (Exception e)
			{
				Log.warning("Failed to create extend class (wrong constructor?). "+whatToExtend.getClass().getSimpleName()+", "+villageVarsClass.getClass().getSimpleName());
				e.printStackTrace();
				return null;
			}

			extendMap.put(villageVarsClass, extend);

			// ищем - был ли уже такой объект
			String globalObjectName = "extend_"+whatToExtend.getClass().getSimpleName()+"_"+whatToExtend.hashCode()+"_"+villageVarsClass.getSimpleName();
			Object oldObject = globalObjectMap.get(globalObjectName);

			processor.transferFields(oldObject, extend);

			try
			{
				processor.process(extend);
			}
			catch (Exception e)
			{
				Log.warning("Failed to process extended class. "+whatToExtend.getClass().getSimpleName()+", "+villageVarsClass.getClass().getSimpleName());
				e.printStackTrace();
				return null;
			}

			globalObjectMap.put(globalObjectName, extend);
		}

		return (T) extend;
	}

}
