package ru.es.annotation;

import ru.es.lang.MultiKeyMap;

import java.util.List;

public class JsonRepository<T> extends MultiKeyMap<T>
{
	private final SerializeManager serializeManager;

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
	}
}
