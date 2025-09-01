package ru.es.thread;

import ru.es.log.Log;

import java.util.HashMap;
import java.util.Map;

public class ThreadPool
{
	public Map<String, SimpleThreadPool> oneThreadPool = new HashMap<>();
	public Map<String, ManagedThread> managed = new HashMap<>();

	public SimpleThreadPool get(String name)
	{
		var ret = oneThreadPool.get(name);

		if (ret == null)
		{
			Log.warning("create thread pool "+name+" (1 thread)");
			ret = new SimpleThreadPool(name, 1, 1);
			oneThreadPool.put(name, ret);
		}

		return ret;
	}

	public ManagedThread getManagedThread(String name)
	{
		var ret = managed.get(name);

		if (ret == null)
		{
			Log.warning("create managed thread: "+name);
			ret = new ManagedThread(name);
			managed.put(name, ret);
		}

		return ret;
	}
}
