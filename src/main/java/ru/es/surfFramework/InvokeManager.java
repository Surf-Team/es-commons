package ru.es.surfFramework;

import ru.es.lang.ESSetterVarargs;
import ru.es.lang.Invoker;
import ru.es.log.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class InvokeManager
{
	private final Map<Class<?>, List<Invoker<?>>> invokersByClass = new HashMap<>();
	private final Map<Object, List<Invoker<?>>> invokersByObject = new HashMap<>();

	public<T> void addInvoker(Invoker<T> invoke)
	{
		var invokersList = invokersByClass.get(invoke.tClass);
		if (invokersList == null)
		{
			invokersList = new ArrayList<>();
			invokersByClass.put(invoke.tClass, invokersList);
		}

		invokersList.add(invoke);
	}


	public<T> void addInvoker(T object, Invoker<T> invoke)
	{
		var invokersList = invokersByObject.get(object);
		if (invokersList == null)
		{
			invokersList = new ArrayList<>();
			invokersByObject.put(object, invokersList);
		}

		invokersList.add(invoke);
	}

	public<T> void addInvoker(Class<T> invokeClass, String shortName, String desc, ESSetterVarargs<T> setter)
	{
		addInvoker(new Invoker<>(invokeClass, shortName, desc, setter));
	}


	public<T> Collection<Invoker<?>> getInvokers(T object)
	{
		Class invokeClass = object.getClass();
		Collection<Invoker<?>> ret = new ArrayList<>();

		var byClass = invokersByClass.get(invokeClass);
		if (byClass != null)
			ret.addAll(byClass);

		var byObject = invokersByObject.get(object);
		if (byObject != null)
			ret.addAll(byObject);

		return ret;
	}

}
