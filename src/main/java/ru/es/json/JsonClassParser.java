package ru.es.json;

import com.google.gson.*;

import java.io.*;
import java.util.*;
import java.util.function.Function;

public class JsonClassParser
{
	private static final Function<String, JsonClassInfo> funcCreator = s -> new JsonClassInfo();


	public Map<String, JsonClassInfo> classProperties = new HashMap<>();
	public List<JsonObject> rootObjects = new ArrayList<>();
	public Map<String, JsonObject> rootObjectsByName = new HashMap<>();
	public Map<String, JsonObject> rootObjectsByNamev2 = new HashMap<>();

	public JsonClassParser(List<File> files) throws FileNotFoundException
	{
		for (File f : files)
		{
			//Log.warning("Processing "+f.getName());

			BufferedReader bufferedReader = new BufferedReader(new FileReader(f));
			JsonArray root = JsonParser.parseReader(bufferedReader).getAsJsonArray();
			for (JsonElement jsonElement : root)
			{
				JsonObject object = jsonElement.getAsJsonObject();
				rootObjects.add(object);

				String objName = object.get("object_name").getAsString();
				String innerName = object.get("innerName").getAsString();
				if (innerName.contains("."))
					innerName = innerName.substring(innerName.indexOf(".")+1);

				String packName = object.get("package").getAsString();

				objName = objName.toLowerCase(Locale.ROOT);

				rootObjectsByName.put(objName, object);
				rootObjectsByNamev2.put((packName+"."+innerName).toLowerCase(Locale.ROOT), object);

				String className = object.get("class").getAsString();

				var classInfo = classProperties.computeIfAbsent(className, funcCreator);

				processObject(object, classInfo);
			}
		}
	}

	public JsonClassParser(JsonArray array) throws FileNotFoundException
	{
		for (JsonElement jsonElement : array)
		{
			JsonObject object = jsonElement.getAsJsonObject();
			rootObjects.add(object);

			String className = "info";
			try
			{
				className = object.get("class").getAsString();
			}
			catch (Exception e)
			{
				//e.printStackTrace();
			}

			var classInfo = classProperties.computeIfAbsent(className, funcCreator);

			processObject(object, classInfo);
		}
	}

	private void processObject(JsonObject parentObject, JsonClassInfo parentClassInfo)
	{
		for (var e : parentObject.entrySet())
		{
			String key = e.getKey();
			var value = e.getValue();

			if (value instanceof JsonArray)
			{
				JsonArray objects = value.getAsJsonArray();
				JsonElement objectInArray = objects.get(0);

				if (objectInArray instanceof JsonObject)
				{
					var subClass = parentClassInfo.arrays.computeIfAbsent(key, funcCreator);
					var subSubClass = subClass.arrays.computeIfAbsent(key, funcCreator);

					//var subSubClass = parentClassInfo.arrays.computeIfAbsent(key, funcCreator);

					for (var subObj : objects)
					{
						processObject(subObj.getAsJsonObject(), subSubClass);
					}
				}
				else if (objectInArray instanceof JsonPrimitive)
				{
					var subClass = parentClassInfo.arrays.computeIfAbsent(key, funcCreator);
					subClass.primitiveFields.add(key);
					//parentClassInfo.primitiveFields.add(key);
				} 
			}
			else if (value instanceof JsonObject)
			{
				var subClass = parentClassInfo.objects.computeIfAbsent(key, funcCreator);

				JsonObject object = value.getAsJsonObject();

				processObject(object, subClass);
			}
			else if (value instanceof JsonPrimitive)
			{
				parentClassInfo.primitiveFields.add(key);
				parentClassInfo.addFieldExample(key, value.getAsString());
			}
			else
			{
				throw new RuntimeException("unknown class; "+value.getClass());
			}
		}
	}
}
