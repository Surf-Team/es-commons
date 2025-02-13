package ru.es.annotation;

import java.net.URL;

public class JsonFileLink implements CollectionLink
{
	public URL url;
	public String fileName;


	public JsonFileLink()
	{
	}

	@Override
	public String toString()
	{
		return url.toString();
	}
}
