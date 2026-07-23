package ru.es.annotation;

import com.google.gson.JsonObject;
import org.yaml.snakeyaml.Yaml;
import ru.es.lang.MultiKeyMap;
import ru.es.lang.ObjectMap;
import ru.es.log.Log;
import ru.es.reflection.ReflectionUtils;
import ru.es.yaml.YamlToJsonConverter;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class YamlDataMapper
{
	private final SurfJsonReader reader;
	private final DependencyManager dependencyManager;
	private final SerializeManager serializeManager;
	private URL yamlRoot;

	public YamlDataMapper(DependencyManager dependencyManager, SerializeManager serializeManager, SurfJsonReader reader)
	{
		this.dependencyManager = dependencyManager;
		this.serializeManager = serializeManager;
		this.reader = reader;
	}

	public void setRootUrl(URL yamlRoot)
	{
		this.yamlRoot = yamlRoot;
	}

	public URL getRootUrl()
	{
		return yamlRoot;
	}

	public YamlFileLink getLink(String file) throws Exception
	{
		var loadedURL = new URL(yamlRoot + file);

		YamlFileLink link = new YamlFileLink();
		link.fileName = file;
		link.url = loadedURL;

		return link;
	}

	@SuppressWarnings("unchecked")
	public <T> void reload(Class<T> tClass, YamlFileLink link) throws Exception
	{
		List<T> arrayList = new ArrayList<>();

		try (var stream = link.url.openStream())
		{
			String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
			Yaml yaml = new Yaml();
			Object parsed = yaml.load(content);

			if (parsed instanceof List)
			{
				List<Object> rootList = (List<Object>) parsed;
				for (Object item : rootList)
				{
					if (item instanceof Map)
					{
						JsonObject jsonObject = YamlToJsonConverter.convert((Map<String, Object>) item);
						T object = reader.parseObject(jsonObject, tClass, null, null);
						arrayList.add(object);
					}
					else
						throw new Exception("YAML collection item is not a map object: " + item);
				}
			}
			else
				throw new Exception("YAML root element must be a list, got: " + (parsed == null ? "null" : parsed.getClass()));
		}

		// deduplicate by key (same as JsonDataMapper)
		Map<Object, T> reMap = new LinkedHashMap<>();
		for (var object : arrayList)
		{
			Object key = AnnotatedUtils.getKey(object);
			reMap.put(key, object);
		}
		arrayList.clear();
		arrayList.addAll(reMap.values());

		ObjectMap<T> existCollection = dependencyManager.getCollection(tClass);
		if (existCollection != null)
		{
			MultiKeyMap<T> newCollection = new MultiKeyMap<>(arrayList, tClass);
			int updatedItems = 0;
			List<T> toRemove = new ArrayList<>();
			for (var oldObject : existCollection.getObjects())
			{
				updatedItems++;
				Object key = AnnotatedUtils.getKey(oldObject);
				var newObject = newCollection.get(key);

				if (newObject == null)
				{
					Log.warning("New object dont found with id: " + key + ". removing");
					toRemove.add(oldObject);
					continue;
				}
				ReflectionUtils.copy(newObject, oldObject);
				dependencyManager.objectChanged(tClass, oldObject);
				newCollection.remove(newObject);
			}
			for (var v : toRemove)
				existCollection.remove(v);

			for (var object : newCollection.getObjects())
			{
				existCollection.add(object);
				dependencyManager.objectChanged(tClass, object);
			}
			if (!newCollection.getObjects().isEmpty())
				existCollection.sort();
			Log.warning("SerializeManager[" + tClass.getSimpleName() + "]: Added new items after reload: " + newCollection.size() + ", updatedItems: " + updatedItems);
		}
		else
		{
			JsonRepository<T> repo = new JsonRepository<>(arrayList, tClass, serializeManager);
			dependencyManager.addCollection(tClass, repo);
			for (var o : repo.objectsRef)
				dependencyManager.objectChanged(tClass, o);
		}
		dependencyManager.objectsReloaded(tClass);
	}
}
