package ru.es.lang;

import java.util.ArrayList;
import java.util.List;

public class ESListener
{
	public List<Runnable> listeners = new ArrayList<>();

	public void addListener(Runnable t)
	{
		listeners.add(t);
	}

	public void removeListener(Runnable t)
	{
		listeners.remove(t);
	}

	public void clearListeners()
	{
		listeners.clear();
	}

	public void event()
	{
		for (Runnable listener : listeners)
		{
			listener.run();
		}
	}
}
