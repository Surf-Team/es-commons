package ru.es.annotation;

import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class JsonMultiFileLink<T> implements CollectionLink
{
	public URL rootUrl;
	public String rootFileName;
	public URL baseUrl;
	public Function<T, String> fileNameFunction;
	public final Set<String> loadedFileNames = new LinkedHashSet<>();
	public String legacyFileName;

	public JsonMultiFileLink()
	{
	}

	@Override
	public String toString()
	{
		return rootUrl != null ? rootUrl.toString() : rootFileName;
	}
}
