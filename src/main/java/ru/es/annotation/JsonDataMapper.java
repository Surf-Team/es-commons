package ru.es.annotation;

import ru.es.lang.MultiKeyMap;
import ru.es.lang.ObjectMap;
import ru.es.log.Log;
import ru.es.reflection.ReflectionUtils;
import ru.es.util.FileUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonDataMapper
{
	public final SurfJsonWriter writer;
	public final SurfJsonReader reader;
	private final DependencyManager dependencyManager;
	private final SerializeManager serializeManager;
	private URL jsonRoot;

	public JsonDataMapper(DependencyManager dependencyManager, SerializeManager serializeManager)
	{
		writer = new SurfJsonWriter(dependencyManager);
		reader = new SurfJsonReader(dependencyManager);
		this.dependencyManager = dependencyManager;
		this.serializeManager = serializeManager;
	}

	public JsonFileLink getLink(String file) throws MalformedURLException
	{
		var loadedURL = new URL(jsonRoot+file);

		JsonFileLink link = new JsonFileLink();
		link.fileName = file;
		link.url = loadedURL;

		return link;
	}


	public void setRootUrl(URL jsonRoot)
	{
		this.jsonRoot = jsonRoot;
	}

	public<T> void reload(Class<T> tClass, JsonFileLink link) throws Exception
	{
		List<T> arrayList = reader.getCollection(tClass, link.url);

		// удаляем повторяющиеся значения
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
			// для элементов, которые уже есть с заданным ключём - просто копируем значения
			List<T> toRemove = new ArrayList<>();
			for (var oldObject : existCollection.getObjects())
			{
				updatedItems++;
				Object key = AnnotatedUtils.getKey(oldObject);
				var newObject = newCollection.get(key);

				if (newObject == null)
				{
					Log.warning("New object dont found with id: "+key+". removing");
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
			Log.warning("SerializeManager["+tClass.getSimpleName()+"]: Added new items after reload: "+newCollection.size()+", updatedItems: "+updatedItems);
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

	public <T> void save(List<T> collection, JsonFileLink link) throws Exception
	{
		FileUtils.writeToURL(link.url, writer.fillCollection(collection).getBytes(StandardCharsets.UTF_8));
		Log.warning("Saved to "+link.toString());
	}

	public <T> MultiKeyMap<T> createCollection(Class<T> tClass, List<T> list)
	{
		JsonRepository<T> repo = new JsonRepository<>(list, tClass, serializeManager);
		return repo;
	}


}
