package ru.es.surfFramework;

import org.apache.commons.lang3.ClassUtils;
import ru.es.log.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class SurfObjectCollection<T>
{
	private final Class<T> objectClass;
	private final SurfFramework framework;

	public final List<T> objects = new ArrayList<>();

	public SurfObjectCollection(Class<T> objectClass, SurfFramework framework)
	{
		this.objectClass = objectClass;

		this.framework = framework;
	}

	public void process()
	{
		int objects = 0;
		for (Class c : framework.getAllClasses())
		{
			if(!ClassUtils.isAssignable(c, objectClass))
				continue;

			if(Modifier.isAbstract(c.getModifiers()))
				continue;

			if (c == objectClass)
				continue;
			try
			{
				Constructor<?> constructor;
				try
				{
					constructor = c.getConstructor();
				}
				catch (NoSuchMethodException methodException)
				{
					Log.warning("Pass init object: "+c.getSimpleName());
					continue;
				}

				// create instance
				T newInstance = (T) constructor.newInstance();

				framework.processor.process(newInstance, c);

				this.objects.add(newInstance);
				framework.singletons.put(newInstance.getClass(), newInstance);
				objects++;
			}
			catch (Exception e)
			{
				Log.warning("SurfFramework: object collection ("+objectClass.getSimpleName()+"): Cant register  class: "+c.getSimpleName());
				e.printStackTrace();
			}
		}
		
		Log.warning("SurfFramework: object collection ("+objectClass.getSimpleName()+"): created "+objects+" objects.");
	}

}
