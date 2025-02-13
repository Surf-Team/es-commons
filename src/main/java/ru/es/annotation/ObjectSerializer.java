package ru.es.annotation;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.es.util.JSONUtils;

import java.io.IOException;
import java.net.URL;

public class ObjectSerializer<T>
{
	private final URL url;
	private final Class<T> tClass;

	private T object;

	public ObjectSerializer(URL url, Class<T> tClass) throws IOException
	{
		this.url = url;
		this.tClass = tClass;
		reload();
	}

	public void reload() throws IOException
	{
		ObjectMapper deSerializeMapper = new ObjectMapper();
		deSerializeMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		object = JSONUtils.load(url, tClass, deSerializeMapper);
	}

	public T get()
	{
		return object;
	}
}
