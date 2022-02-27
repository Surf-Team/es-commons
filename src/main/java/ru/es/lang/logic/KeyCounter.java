package ru.es.lang.logic;

import ru.es.lang.Value;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// счётчик для ключей K с возможностью быстрого получения отсортированного списка ключей
public class KeyCounter<K>
{
	private Map<K, Value<Integer>> count = new ConcurrentHashMap<>();

	public void increment(K key)
	{
		var value = count.get(key);
		if (value == null)
		{
			value = new Value<>(0);
			count.put(key, value);
		}

		value.set(value.get() + 1);
	}

	public List<K> getSortedKeys(boolean bigFirst)
	{
		int mult = bigFirst ? -1 : 1;

		List<K> ret = new ArrayList<>();
		ret.addAll(count.keySet());
		ret.sort(Comparator.comparingInt(value -> count.get(value).get()*mult));
		return ret;
	}

	public int getValue(K key)
	{
		return count.get(key).get();
	}

	public void remove(K activeChar)
	{
		count.remove(activeChar);
	}

	public void clear()
	{
		count.clear();
	}
}
