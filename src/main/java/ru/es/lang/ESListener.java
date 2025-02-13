package ru.es.lang;

import ru.es.log.Log;

import java.util.ArrayList;
import java.util.List;

public class ESListener implements Runnable
{
	public List<Runnable> listeners = new ArrayList<>();

	public void addListener(Runnable t)
	{
		listeners.add(t);
	}

	public void addListenerAndRun(Runnable r)
	{
		Log.warning("add and run!!");
		listeners.add(r);
		Log.warning("run!");
		r.run();
		Log.warning("runned!");
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

	@Override
	public void run()
	{
		event();
	}
}
