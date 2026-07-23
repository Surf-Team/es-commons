package ru.es.annotation;

import java.net.URL;

public class YamlFileLink implements CollectionLink
{
	public URL url;
	public String fileName;

	public YamlFileLink()
	{
	}

	@Override
	public String toString()
	{
		return fileName;
	}
}
