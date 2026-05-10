package ru.es.annotation;

import ru.es.lang.MultiKeyMap;
import ru.es.util.ListUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JsonRepository<T> extends MultiKeyMap<T>
{
	private final SerializeManager serializeManager;
	private Comparator<T> comparator;

	public JsonRepository(List<T> list, Class<T> tClass, SerializeManager serializeManager)
	{
		super(list, tClass);
		saveable = true;
		this.serializeManager = serializeManager;
	}

	@Override
	public void save() throws Exception
	{
		serializeManager.save(tClass);
	}

	@Override
	public void reload() throws Exception
	{
		serializeManager.reload(tClass);
		if (comparator != null)
			sort();
	}

	@Override
	public void setSort(Comparator<T> comparator)
	{
		this.comparator = comparator;
	}

	public void sort()
	{
		var newList = ListUtils.createList(unmodificableList);
		newList.sort(comparator);

		this.unmodificableList = Collections.unmodifiableList(newList);
	}
}
