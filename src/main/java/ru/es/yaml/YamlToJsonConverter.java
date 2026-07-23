package ru.es.yaml;

import com.google.gson.*;

import java.util.List;
import java.util.Map;

public class YamlToJsonConverter
{
	public static JsonObject convert(Map<String, Object> yamlMap)
	{
		JsonObject jsonObject = new JsonObject();
		for (Map.Entry<String, Object> entry : yamlMap.entrySet())
		{
			jsonObject.add(entry.getKey(), convertValue(entry.getValue()));
		}
		return jsonObject;
	}

	public static JsonArray convertList(List<Object> yamlList)
	{
		JsonArray jsonArray = new JsonArray();
		for (Object item : yamlList)
		{
			jsonArray.add(convertValue(item));
		}
		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	private static JsonElement convertValue(Object value)
	{
		if (value == null)
			return JsonNull.INSTANCE;
		if (value instanceof Map)
			return convert((Map<String, Object>) value);
		if (value instanceof List)
			return convertList((List<Object>) value);
		if (value instanceof Boolean)
			return new JsonPrimitive((Boolean) value);
		if (value instanceof Number)
			return new JsonPrimitive((Number) value);
		return new JsonPrimitive(value.toString());
	}
}
