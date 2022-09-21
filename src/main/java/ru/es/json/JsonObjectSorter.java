package ru.es.json;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import ru.es.log.Log;
import ru.es.util.FileUtils;
import ru.es.util.JSONUtils;
import ru.es.util.ListUtils;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class JsonObjectSorter
{
	public JsonObjectSorter(List<File> jsonFiles, File outFolder) throws IOException
	{
		var func = new Function<String, Map<String, JsonElement>>() {
			@Override
			public Map<String, JsonElement> apply(String s)
			{
				return new HashMap<>();
			}
		};

		Map<String, Map<String, JsonElement>> map = new HashMap<>();

		for (File f : jsonFiles)
		{
			Log.warning("Processing "+f.getName());

			BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
			JsonArray root = JsonParser.parseReader(bufferedReader).getAsJsonArray();
			for (JsonElement jsonElement : root)
			{
				JsonObject object = jsonElement.getAsJsonObject();

				String className = object.get("class").getAsString();

				var mapInternal = map.computeIfAbsent(className, func);
				mapInternal.put(object.get("object_name").getAsString(), object);
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonWriter writer = gson.newJsonWriter(new StringWriter());



		for (String objectName : sort(map.keySet()))
		{
			JsonArray jsonArray = new JsonArray();

			var internal = map.get(objectName);

			for (String internalObjectName : internal.keySet())
			{
				var obj = internal.get(internalObjectName);
				jsonArray.add(obj);
			}
			
			String[] splitted = objectName.split("\\.");

			String text = gson.toJson(jsonArray);
			FileUtils.writeFile(new File(outFolder, splitted[splitted.length-1]+".json"), text);
		}

	}

	private static List<String> sort(Collection<String> collection)
	{
		var sortedList = ListUtils.createList(collection);
		Collections.sort(sortedList);

		return sortedList;
	}
}
