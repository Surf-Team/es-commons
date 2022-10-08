package ru.es.surfFramework;

import org.apache.commons.lang3.ClassUtils;
import ru.es.log.Log;

import java.util.ArrayList;
import java.util.List;

public class SurfObjectCollection<T>
{
	public final List<T> objects = new ArrayList<>();

	SurfObjectCollection(Class<T> objectClass, SurfFramework framework)
	{
		int objects = 0;
		for (Class c : framework.getAllClasses())
		{
			if(!ClassUtils.isAssignable(c, objectClass))
				continue;

			if (c == objectClass)
				continue;
			try
			{
				// create instance
				T newInstance = (T) c.getConstructor().newInstance();

				framework.processor.process(newInstance, c);

				this.objects.add(newInstance);
				framework.singletons.put(newInstance.getClass(), newInstance);
				objects++;
			}
			catch (Exception e)
			{
				Log.warning("SurfObjectCollection ("+objectClass.getSimpleName()+"): Cant register  class: "+c.getSimpleName());
				e.printStackTrace();
			}
		}
		Log.warning("SurfObjectCollection ("+objectClass.getSimpleName()+"): created "+objects+" objects.");
	}

}
