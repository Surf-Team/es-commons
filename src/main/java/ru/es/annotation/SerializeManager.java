package ru.es.annotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.es.lang.ESThrowingEventHandler;
import ru.es.lang.MultiKeyMap;
import ru.es.lang.ObjectMap;
import ru.es.log.Log;
import ru.es.util.ListUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class SerializeManager
{
	private final DependencyManager dependencyManager;

	public JsonDataMapper jsonDataMapper;
	public final ESThrowingEventHandler onReload = new ESThrowingEventHandler();

	Map<Class, CollectionLink> loadedLinks = new HashMap<>();

	public SerializeManager(DependencyManager dependencyManager)
	{
		this.dependencyManager = dependencyManager;
		jsonDataMapper = new JsonDataMapper(dependencyManager, this);
	}

	public void addLink(Class cClass, CollectionLink link)
	{
		loadedLinks.put(cClass, link);
	}

	public <T> void addSupplementalLoader(Class<T> tClass, Callable<List<T>> loader)
	{
		jsonDataMapper.addSupplementalLoader(tClass, loader);
	}

	// инициализация сериализации в json
	public void initJSON(URL jsonRoot)
	{
		jsonDataMapper.setRootUrl(jsonRoot);
	}


	// ############################################
	// ################## JSON ####################
	// ############################################


	// добавляет JSON коллекцию из файла в DependencyManager
	public<T> ObjectMap<T> addCollection(Class<T> tClass, String file) throws Exception
	{
		return addCollection(tClass, file, false);
	}

	// позволяет добавлять не существующие коллекции (например для новых классов), которые потом сохранятся
	public<T> ObjectMap<T> addCollection(Class<T> tClass, String file, boolean allowNonExistent) throws Exception
	{
		if (!allowNonExistent)
		{
			var link = jsonDataMapper.getLink(file);
			return addCollection(tClass, link, true);
		}
		else
		{
			try
			{
				var link = jsonDataMapper.getLink(file);
				return addCollection(tClass, link, true);
			}
			catch (Exception e)
			{
				Log.warning("Adding new collection: "+tClass.getSimpleName()+" -> "+file);
				var collection = addNewCollection(tClass, new ArrayList<>(), file);
				save(tClass);
				return collection;
			}
		}
	}


	public<T> ObjectMap<T> addCollection(Class<T> tClass, CollectionLink link, boolean load) throws Exception
	{
		loadedLinks.put(tClass, link);

		if (load)
			reload(tClass);

		return dependencyManager.getCollection(tClass);
	}



	// добавляет коллекцию из файла в DependencyManager
	public<T> MultiKeyMap<T> addNewCollection(Class<T> tClass, List<T> list, String file) throws IOException
	{
		var link = jsonDataMapper.getLink(file);

		loadedLinks.put(tClass, link);

		MultiKeyMap<T> repo = jsonDataMapper.createCollection(tClass, list);
		dependencyManager.addCollection(tClass, repo);
		return repo;
	}



	// добавляет JSON коллекцию из нескольких файлов в DependencyManager
	public<T> ObjectMap<T> addMultiFileCollection(Class<T> tClass, String rootFile, Function<T, String> fileNameFunc) throws Exception
	{
		return addMultiFileCollection(tClass, rootFile, fileNameFunc, null);
	}

	// legacyFile — старый единый файл; если указан, загружается и коллекция сохраняется в новый формат автоматически
	public<T> ObjectMap<T> addMultiFileCollection(Class<T> tClass, String rootFile, Function<T, String> fileNameFunc, String legacyFile) throws Exception
	{
		var link = jsonDataMapper.getMultiFileLink(rootFile, fileNameFunc, legacyFile);
		return addCollection(tClass, link, true);
	}

	// package private
	<T> void save(Class<T> tClass) throws Exception
	{
		var link = loadedLinks.get(tClass); // not loaded from manager
		if (link == null)
			return;


		var collection = dependencyManager.getCollection(tClass);

		if (!collection.isSaveable())
		{
			Log.warning("SerializeManager: NOT SAVEABLE collection of " + tClass.getSimpleName() + "... to " + link);
			return;
		}

		var list = collection.getObjects();

		Log.warning("SerializeManager: saving collection of "+tClass.getSimpleName()+"... to "+link);

		if (link instanceof JsonFileLink)
			jsonDataMapper.save(list, (JsonFileLink) link);
		else if (link instanceof JsonMultiFileLink)
			jsonDataMapper.saveMultiFile(list, (JsonMultiFileLink<T>) link);
		else
			Log.warning("No data mapper support for class: "+tClass.getSimpleName());
	}


	<T> void reload(Class<T> tClass) throws Exception
	{
		Log.warning("Loading "+tClass.getSimpleName()+" collection...");
		var link = loadedLinks.get(tClass);

		if (link instanceof JsonFileLink)
			jsonDataMapper.reload(tClass, (JsonFileLink) link);
		else if (link instanceof JsonMultiFileLink)
			jsonDataMapper.reloadMultiFile(tClass, (JsonMultiFileLink<T>) link);
		else
			Log.warning("No data mapper support for class: "+tClass.getSimpleName());
	}



	public void reloadAll() throws Exception
	{
		for (var e : loadedLinks.entrySet())
		{
			Log.warning("Reloading collection: "+e.getKey().getSimpleName());
			reload(e.getKey());
		}
		onReload.event(null);
	}

	public Object copyObject(Object object) throws Exception
	{
		// копируем всегда с помощью json вне зависимости от типа объекта
		JsonElement jsonElement = jsonDataMapper.writer.getObject(object);
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		return jsonDataMapper.reader.parseObject(jsonObject, object.getClass(), null, null);
    }

	public CollectionLink getLink(Class c)
	{
		return loadedLinks.get(c);
	}

	public void changeRoot(URL newRoot) throws MalformedURLException
	{
		Log.warning("set new root: "+newRoot);
		initJSON(newRoot);
		for (var k : ListUtils.createList(loadedLinks.keySet()))
		{
			var exist = getLink(k);
			if (exist instanceof JsonFileLink)
				loadedLinks.put(k, jsonDataMapper.getLink(((JsonFileLink) exist).fileName));
			else if (exist instanceof JsonMultiFileLink)
			{
				var multiLink = (JsonMultiFileLink) exist;
				loadedLinks.put(k, jsonDataMapper.getMultiFileLink(multiLink.rootFileName, multiLink.fileNameFunction));
			}
		}
	}
}
