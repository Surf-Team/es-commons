package ru.es.lang.limiters;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CountTimeLimiterV2
{
	public ConcurrentHashMap<String, Data> map = new ConcurrentHashMap<>();

	public class Data
	{
		List<Long> lastActions = new CopyOnWriteArrayList<>();
	}

	public boolean allow(String key, int maxCount, long time)
	{
		var data = map.get(key);
		if (data == null)
		{
			data = new Data();
			map.put(key, data);
		}

		long curTime = System.currentTimeMillis();
		int count = 0;
		for (var l : data.lastActions)
		{
			if (l + time > curTime)
				count++;
		}

		if (count >= maxCount)
			return false;

		data.lastActions.add(curTime);

		return true;
	}
}
