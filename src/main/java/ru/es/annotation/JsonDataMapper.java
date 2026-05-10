package ru.es.annotation;

import ru.es.lang.MultiKeyMap;
import ru.es.lang.ObjectMap;
import ru.es.log.Log;
import ru.es.reflection.ReflectionUtils;
import ru.es.util.FileUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

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


	public<T> JsonMultiFileLink<T> getMultiFileLink(String rootFile, Function<T, String> fileNameFunc) throws MalformedURLException
	{
		return getMultiFileLink(rootFile, fileNameFunc, null);
	}

	public<T> JsonMultiFileLink<T> getMultiFileLink(String rootFile, Function<T, String> fileNameFunc, String legacyFile) throws MalformedURLException
	{
		JsonMultiFileLink<T> link = new JsonMultiFileLink<>();
		link.rootFileName = rootFile;
		link.rootUrl = new URL(jsonRoot + rootFile);
		link.baseUrl = jsonRoot;
		link.fileNameFunction = fileNameFunc;
		link.legacyFileName = legacyFile;
		return link;
	}

	public<T> void reloadMultiFile(Class<T> tClass, JsonMultiFileLink<T> link) throws Exception
	{
		List<String> fileNames;
		try
		{
			fileNames = readFileList(link.rootUrl);
		}
		catch (Exception e)
		{
			Log.warning("Multi-file root not found: " + link.rootUrl + ", initializing empty collection.");
			fileNames = new ArrayList<>();
		}

		link.loadedFileNames.clear();
		link.loadedFileNames.addAll(fileNames);

		List<T> allItems = new ArrayList<>();
		for (String fileName : fileNames)
		{
			URL fileUrl = new URL(link.baseUrl.toString() + fileName);
			List<T> items = reader.getCollection(tClass, fileUrl);
			allItems.addAll(items);
		}

		boolean legacyLoaded = false;
		if (link.legacyFileName != null)
		{
			try
			{
				URL legacyUrl = new URL(link.baseUrl.toString() + link.legacyFileName);
				List<T> legacyItems = reader.getCollection(tClass, legacyUrl);
				allItems.addAll(legacyItems);
				legacyLoaded = !legacyItems.isEmpty();
				Log.warning("Loaded legacy file: " + link.legacyFileName + " (" + legacyItems.size() + " items)");
			}
			catch (Exception e)
			{
				Log.warning("Legacy file not found or failed to load: " + link.legacyFileName);
			}
		}

		Map<Object, T> reMap = new LinkedHashMap<>();
		for (var object : allItems)
		{
			Object key = AnnotatedUtils.getKey(object);
			reMap.put(key, object);
		}
		allItems.clear();
		allItems.addAll(reMap.values());

		ObjectMap<T> existCollection = dependencyManager.getCollection(tClass);
		if (existCollection != null)
		{
			MultiKeyMap<T> newCollection = new MultiKeyMap<>(allItems, tClass);
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
			Log.warning("SerializeManager[" + tClass.getSimpleName() + "]: Added new items after reload: " + newCollection.size() + ", updatedItems: " + updatedItems);
		}
		else
		{
			JsonRepository<T> repo = new JsonRepository<>(allItems, tClass, serializeManager);
			dependencyManager.addCollection(tClass, repo);
			for (var o : repo.objectsRef)
				dependencyManager.objectChanged(tClass, o);
		}
		dependencyManager.objectsReloaded(tClass);

		if (legacyLoaded)
		{
			Log.warning("Legacy file loaded, auto-saving to multi-file format...");
			saveMultiFile(dependencyManager.getCollection(tClass).getObjects(), link);
		}
	}

	public<T> void saveMultiFile(List<T> collection, JsonMultiFileLink<T> link) throws Exception
	{
		Map<String, List<T>> byFile = new LinkedHashMap<>();
		for (String fileName : link.loadedFileNames)
			byFile.put(fileName, new ArrayList<>());

		for (T item : collection)
		{
			String fileName = link.fileNameFunction.apply(item);
			byFile.computeIfAbsent(fileName, k -> new ArrayList<>()).add(item);
			link.loadedFileNames.add(fileName);
		}

		for (Map.Entry<String, List<T>> entry : byFile.entrySet())
		{
			URL fileUrl = new URL(link.baseUrl.toString() + entry.getKey());
			FileUtils.writeToURL(fileUrl, writer.fillCollection(entry.getValue()).getBytes(StandardCharsets.UTF_8));
			Log.warning("Saved to " + fileUrl);
		}

		List<String> nonEmptyFiles = new ArrayList<>();
		for (var e : byFile.entrySet())
			if (!e.getValue().isEmpty())
				nonEmptyFiles.add(e.getKey());

		FileUtils.writeToURL(link.rootUrl, writer.gson.toJson(nonEmptyFiles).getBytes(StandardCharsets.UTF_8));
		Log.warning("Saved root file to " + link.rootUrl);
	}

	private List<String> readFileList(URL url) throws Exception
	{
		try (var stream = url.openStream())
		{
			String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
			JsonArray array = JsonParser.parseString(content).getAsJsonArray();
			List<String> result = new ArrayList<>();
			for (var element : array)
				result.add(element.getAsString());
			return result;
		}
	}
}
