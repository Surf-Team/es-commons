package ru.es.lang;

import java.util.HashMap;
import java.util.Map;

public class ObjectEventHandler
{
	public Map<Class, ESEventHandler> handlerMap = new HashMap<>();

	public<T> void addOnEvent(Class<T> eventClass, ESEvent<T> onEvent)
	{
		var eh = handlerMap.get(eventClass);
		if (eh == null)
		{
			eh = new ESEventHandler();
			handlerMap.put(eventClass, eh);
		}

		eh.addListener(onEvent);
	}

	public void eventReceived(Object o)
	{
		var eh = handlerMap.get(o.getClass());
		if (eh == null)
			return;

		eh.event(o);
	}
}
