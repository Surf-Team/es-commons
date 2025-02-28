package ru.es.annotation;

import ru.es.lang.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

// что позволяет DependencyManager:
// 1. Новые объекты коллекций записываются в коллекцию с коллекциями :) В итоге любую коллекцию можно достать по указанию класса
// 2. Доставать конкретные объекты внутри коллекций по ключу, указав класс объекта и ключ
// 		ключ указывается при обьявлении поля через @UniqueKey
//		у объекта может быть 1 или несколько ключей
public class DependencyManager
{
	private Map<Class, ObjectMap> collections = new HashMap<>();

	private Map<Class, ESEventHandler> onReloadObject = new HashMap<>();
	private Map<Class, ESListener> objectsReloaded = new HashMap<>();

	// заготовленные конвертеры для описаний объектов (если класс числится здесь, то используем это вместо toString())
	private Map<Class, Converter<Object, String>> objectDescriptionConverter = new HashMap<>();
	private final Map<Class, Converter<?, ?>> objectToDTO = new HashMap<>();
	private final Map<Class, Class> dtoToCollectionClass = new HashMap<>();
	private final Map<Class, List<Class>> overrides = new HashMap<>();

	// достать коллекцию по указанному классу
	public<T> ObjectMap<T> getCollection(Class<T> tClass)
	{
		return collections.get(tClass);
	}

	// Доставать объект из коллекции по ключу
	// ключ указывается при обьявлении поля через @UniqueKey
	public<T> T getValue(Class<T> tClass, Object key)
	{
		return (T) getCollection(tClass).get(key);
	}



	public<T> MultiKeyMap<T> addCollection(Class<T> c, List<T> listReference)
	{
		MultiKeyMap<T> repo = new MultiKeyMap<T>(listReference, c);
		collections.put(c, repo);
		return repo;
	}

	public<T> void addCollection(Class<T> c, ObjectMap<T> repo)
	{
		collections.put(c, repo);
	}

	public <T,E> void addDTO(Class<T> c, Class<E> to, Converter<T, E> e)
	{
		objectToDTO.put(c, e);
		dtoToCollectionClass.put(to, c);
	}

	public<T> Converter<T, ?> getDTOConverter(Class<T> c)
	{
		return (Converter<T, ?>) objectToDTO.get(c);
	}

	public Collection<Class> getCollectionClasses()
	{
		return collections.keySet();
	}

	public Map<Class, Class> getDtoToCollectionClass()
	{
		return dtoToCollectionClass;
	}

	public<T> void save(Class<T> c) throws Exception
	{
		getCollection(c).save();
	}


	@Deprecated
	public <T> void objectChanged(Class<T> tClass, T object)
	{
		var eh = onReloadObject.get(tClass);
		if (eh != null)
			eh.event(object);
	}

	// вызывается для проверки целостности значений или для калькуляции readonly значений
	// - при создании объекта
	// - при обновлении с диска
	// - при изменении
	public <T> void objectChanged(T object)
	{
		if (object == null)
			return;

		var eh = onReloadObject.get(object.getClass());
		if (eh != null)
			eh.event(object);
	}

	// добавить событие при перезагрузке объекта
	public <T> void addOnChangeObject(Class<T> tClass, ESEvent<T> event)
	{
		ESEventHandler eh = onReloadObject.get(tClass);
		if (eh == null)
		{
			eh = new ESEventHandler<T>();
			onReloadObject.put(tClass, eh);
		}

		((ESEventHandler<T>) eh).addListener(event);

		var existCollection = getCollection(tClass);
		for (var v : existCollection.getObjects())
		{
			objectChanged(tClass, v);
		}
	}

	public <T> void objectsReloaded(Class<T> tClass)
	{
		var eh = objectsReloaded.get(tClass);
		if (eh != null)
			eh.run();
	}


	// добавить событие при перезагрузке коллекции
	public <T> void addOnCollectionReloaded(Class<T> tClass, boolean runNow, Runnable event)
	{
		ESListener eh = objectsReloaded.get(tClass);
		if (eh == null)
		{
			eh = new ESListener();
			objectsReloaded.put(tClass, eh);
		}

		eh.addListener(event);

		if (runNow)
			event.run();
	}

	public <T> void addObjectDescription(Class<T> tClass, Converter<T, String> converter)
	{
		objectDescriptionConverter.put(tClass, (Converter<Object, String>) converter);
	}

	// получить конвертер по классу
	public Converter<Object, String> getNameConverter(Class tClass)
	{
		return objectDescriptionConverter.get(tClass);
	}


	public<A,B> void addOverride(Class<A> baseClass, Class<B> overrideClass)
	{
		var list = overrides.get(baseClass);
		if (list == null)
		{
			list = new ArrayList<>();
			overrides.put(baseClass, list);
		}
		list.add(overrideClass);
	}

	// возвращает список подклассов, которые могут быть выбраны в качестве значений для поля с типом baseClass
	public List<Class> getAllowedOverrides(Class baseClass)
	{
		return overrides.get(baseClass);
	}

	// получить следующий свободный ID для коллекцими
	// UniqueKey должен быть int
	// уникальных ключей не должно быть несколько
	public int getFreeId(Class oClass) throws IllegalAccessException
	{
		var keyField = AnnotatedUtils.getKeyField(oClass);

		int minId = -1;
		for (Object o : getCollection(oClass).getObjects())
		{
			int value = keyField.getInt(o);

			if (minId < value)
				minId = value;
		}

		return minId + 1;
	}

	public List<Class> getAllOverrides()
	{
		List<Class> result = new ArrayList<>();
		for (var c : overrides.values())
		{
			result.addAll(c);
		}
		return result;
	}
}

