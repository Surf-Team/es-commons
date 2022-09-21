package ru.es.json;


import java.util.*;
import java.util.function.Function;

public class JsonClassInfo
{
	private static final Function<String, Set<String>> funcConstructor = s -> new HashSet<>();

	public Set<String> primitiveFields = new HashSet<>();
	public Map<String, Set<String>> primitiveFieldExamples = new HashMap<>();
	public Map<String, JsonClassInfo> objects = new HashMap<>();
	public Map<String, JsonClassInfo> arrays = new HashMap<>();

	boolean examplesMore = false;
	public void addFieldExample(String fieldName, String value)
	{
		var list = primitiveFieldExamples.computeIfAbsent(fieldName, funcConstructor);
		list.add(value);
	}
}
