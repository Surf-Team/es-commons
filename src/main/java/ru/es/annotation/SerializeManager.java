package ru.es.annotation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.es.lang.ESEventHandler;
import ru.es.lang.MultiKeyMap;
import ru.es.lang.ObjectMap;
import ru.es.log.Log;
import ru.es.util.ListUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class SerializeManager
{
	private final DependencyManager dependencyManager;

	public JsonDataMapper jsonDataMapper;
	public final ESEventHandler onReload = new ESEventHandler();

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
		var link = jsonDataMapper.getLink(file);
		return addCollection(tClass, link, true);
	}


	public<T> ObjectMap<T> addCollection(Class<T> tClass, CollectionLink link, boolean load) throws Exception
	{
		loadedLinks.put(tClass, link);

		if (load)
			reload(tClass);

		return dependencyManager.getCollection(tClass);
	}



	// добавляет коллекцию из файла в DependencyManager
	public<T> void addNewCollection(Class<T> tClass, List<T> list, String file) throws IOException
	{
		var link = jsonDataMapper.getLink(file);

		loadedLinks.put(tClass, link);

		MultiKeyMap<T> repo = jsonDataMapper.createCollection(tClass, list);
		dependencyManager.addCollection(tClass, repo);
	}



	// package private
	<T> void save(Class<T> tClass) throws Exception
	{
		var link = loadedLinks.get(tClass); // not loaded from manager
		if (link == null)
			return;

		Log.warning("SerializeManager: saving collection of "+tClass.getSimpleName()+"... to "+link);

		var collection = dependencyManager.getCollection(tClass).getObjects();

		if (link instanceof JsonFileLink)
			jsonDataMapper.save(collection, (JsonFileLink) link);
		else
			Log.warning("No data mapper support for class: "+tClass.getSimpleName());
	}


	<T> void reload(Class<T> tClass) throws Exception
	{
		Log.warning("Loading "+tClass.getSimpleName()+" collection...");
		var link = loadedLinks.get(tClass);

		if (link instanceof JsonFileLink)
			jsonDataMapper.reload(tClass, (JsonFileLink) link);
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
			loadedLinks.put(k, jsonDataMapper.getLink(((JsonFileLink) exist).fileName));
		}
	}
}
