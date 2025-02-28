package ru.es.annotation;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import ru.es.log.Log;
import ru.es.reflection.ReflectionUtils;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class SurfJsonWriter
{
	public final Gson gson;
	private final DependencyManager dependencyManager;
	public final Map<Class, ListSerializer> listSerializers = new HashMap<>();
	public final Map<Class, ArraySerializer> arraySerializers = new HashMap<>();

	public boolean saveSecrets = true;

	public SurfJsonWriter(DependencyManager dependencyManager)
	{
		this.dependencyManager = dependencyManager;
		gson = new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.disableHtmlEscaping()
				.create();
	}

	public String fillObject(Object object) throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		//JsonWriter jsonWriter = new JsonWriter(stringWriter);
		JsonWriter jsonWriter = gson.newJsonWriter(stringWriter);
		jsonWriter.setIndent("    ");

		JsonElement ret = getObject(object);

		gson.toJson(ret, jsonWriter);
		return replacer(stringWriter);
	}

	public String fillCollection(Collection<?> o) throws Exception
	{
		StringWriter stringWriter = new StringWriter();
		//JsonWriter jsonWriter = new JsonWriter(stringWriter);
		JsonWriter jsonWriter = gson.newJsonWriter(stringWriter);
		jsonWriter.setIndent("    ");

		JsonArray ret = getCollection(o);

		gson.toJson(ret, jsonWriter);
		return replacer(stringWriter);
	}

	private String replacer(StringWriter stringWriter)
	{
		String str = stringWriter.toString();
		// remove later
		str = str.replace("\": ", "\" : ");
		str = str.replace("[]", "[ ]"); // Замена символа новой строки
		str = str.replace("\n", "\r\n"); // Замена символа новой строки
		return str;
	}

	private JsonArray getCollection(Collection<?> currentObject) throws Exception
	{
		JsonArray jsonArray = new JsonArray();
		for (Object o : currentObject)
		{
			boolean allowSave = true;

			try
			{
				for (var m : o.getClass().getMethods())
				{
					if (m.getName().equalsIgnoreCase("_allowSave"))
					{
						allowSave = (Boolean) m.invoke(o);
						break;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			if (!allowSave)
				continue;

			JsonElement element;
			if (o instanceof String)
			{
				element = new JsonPrimitive((String) o);
			}
			else if (isNumberClass(o.getClass()))
			{
				element = new JsonPrimitive((Number) o);
			}
			else
			{
				element = getObject(o);
			}
			jsonArray.add(element);
		}
		return jsonArray;
	}


	public JsonElement getObject(Object object) throws Exception
	{
		if (object == null)
			return JsonNull.INSTANCE;

		JsonObject ret = new JsonObject();

		List<Class> classHierarchy = ReflectionUtils.getClassHierarchy(object.getClass());

		DynamicType dynamicType = null;

		for (Class<?> oClass : classHierarchy)
		{
			if (dynamicType == null)
				dynamicType = oClass.getAnnotation(DynamicType.class);

			for (Field f : oClass.getDeclaredFields())
			{
				try
				{
					if (Modifier.isStatic(f.getModifiers()))
						continue;
					if (Modifier.isPrivate(f.getModifiers()))
						continue;
					if (Modifier.isTransient(f.getModifiers()))
						continue;
					if (f.getAnnotation(JsonIgnore.class) != null)
						continue;
					if (f.getAnnotation(NoSave.class) != null)
						continue;
					if (f.getAnnotation(Parent.class) != null)
						continue;
					if (f.getAnnotation(Secret.class) != null && !saveSecrets)
					{
						ret.add(f.getName(), new JsonPrimitive("SECRET_FIELD"));
						continue;
					}

					var value = f.get(object);

					if (f.getAnnotation(JsonIdentityReference.class) != null &&
							!Map.class.isAssignableFrom(f.getType()) &&
							!Collection.class.isAssignableFrom(f.getType()))
					{
						if (value != null)
							// link write
							ret.add(f.getName(), getElement(AnnotatedUtils.getKey(value)));
						else
							ret.add(f.getName(), JsonNull.INSTANCE);
					}
					else
					{
						// full object write
						ret.add(f.getName(), getElement(value));
					}
				}
				catch (Exception e)
				{
					Log.warning("SurfJson error in class: " + oClass.getSimpleName() + ", field: " + f.getName());

					throw e;
				}
			}
		}

		if (dynamicType != null)
		{
			ret.addProperty(dynamicType.fieldName(), object.getClass().getName());
		}

		return ret;
	}

	private JsonElement getElement(Object value) throws Exception
	{
		if (value == null)
			return JsonNull.INSTANCE;

		Class<?> fieldType = value.getClass();

		if (isNumberClass(fieldType))
			return new JsonPrimitive((Number) value);
		else if (fieldType == boolean.class || fieldType == Boolean.class)
			return new JsonPrimitive((boolean) value);
		else if (fieldType == String.class)
			return new JsonPrimitive((String) value);
		else if (fieldType.isEnum())
			return new JsonPrimitive(((Enum) value).name());
		else if (fieldType.isArray())
		{
			Class<?> componentType = fieldType.getComponentType();

			var serializer = arraySerializers.get(componentType);

			if (serializer != null)
				return serializer.serialize((Object[]) value);

			if (componentType == String.class)
				return getPrimitiveArray((String[]) value);
			if (componentType == Boolean.class || componentType == boolean.class)
				return getBoolArray((Boolean[]) value);
			else if (componentType == int.class)
				return getNumberArray((int[]) value);
			else if (componentType == byte.class)
				return getNumberArray((byte[]) value);
			else if (componentType == float.class)
				return getNumberArray((float[]) value);
			else if (componentType == double.class)
				return getNumberArray((double[]) value);
			else if (componentType == long.class)
				return getNumberArray((long[]) value);
			else if (componentType == short.class)
				return getNumberArray((short[]) value);
			else if (componentType == Integer.class)
				return getNumberArray((Integer[]) value);
			else if (componentType == Float.class)
				return getNumberArray((Float[]) value);
			else if (componentType == Double.class)
				return getNumberArray((Double[]) value);
			else if (componentType == Byte.class)
				return getNumberArray((Byte[]) value);
			else if (componentType == Long.class)
				return getNumberArray((Long[]) value);
			else if (componentType == Short.class)
				return getNumberArray((Short[]) value);
			else
			{
				return getArray((Object[]) value);
			}
		}
		else if (Collection.class.isAssignableFrom(fieldType)) // collections
		{
			var collection = (Collection<?>) value;

			if (collection.isEmpty())
				return new JsonArray();
			else
			{
				var firstObject = collection.iterator().next();
				var collectedClass = firstObject.getClass();

				// проверяем - являются ли объекты в списке залинкованными
				boolean isLink = isLink(collectedClass);

				var serializer = listSerializers.get(collectedClass);

				if (serializer != null)
					return serializer.serialize(collection);
				else if (isLink)
				{
					JsonArray array = new JsonArray();
					for (var e : collection)
					{
						array.add(new JsonPrimitive(AnnotatedUtils.getKey(e).toString()));
					}
					return array;
				}
				else
				{
					JsonArray array = new JsonArray();
					for (var e : collection)
					{
						array.add(getElement(e));
					}
					return array;
				}
			}
		}
		else if (Map.class.isAssignableFrom(fieldType)) // collections
		{
			var map = (Map<?, ?>) value;

			JsonObject mapObject = new JsonObject();
			
			for (var e : map.entrySet())
			{
				mapObject.add(e.getKey().toString(), getElement(e.getValue()));
			}
			return mapObject;
		}
		else
		{
			return getObject(value);
		}
	}

	private boolean isLink(Class<?> collectedClass)
	{
		return dependencyManager.getCollectionClasses().contains(collectedClass)
				|| dependencyManager.getAllOverrides().contains(collectedClass);
	}

	private JsonElement getBoolArray(Boolean[] value)
	{
		JsonArray jsonArray = new JsonArray();
		for (Boolean o : value)
		{
			if (o != null)
				jsonArray.add(new JsonPrimitive(o));
			else
				jsonArray.add(JsonNull.INSTANCE);
		}
		return jsonArray;
	}

	private boolean isNumberClass(Class<?> type)
	{
		return type == int.class || type == double.class || type == float.class || type == short.class || type == long.class || type == byte.class ||
				type == Integer.class || type == Double.class || type == Float.class || type == Short.class || type == Long.class || type == Byte.class;
	}


	private<T> JsonArray getPrimitiveArray(T[] currentObject) throws IllegalAccessException
	{
		JsonArray jsonArray = new JsonArray();
		for (T o : currentObject)
		{
			if (o != null)
				jsonArray.add(new JsonPrimitive(o.toString()));
			else
				jsonArray.add(new JsonPrimitive(""));
		}
		return jsonArray;
	}

	private<T> JsonArray getArray(T[] currentObject) throws Exception
	{
		JsonArray jsonArray = new JsonArray();
		for (T o : currentObject)
		{
			if (o == null)
				continue; //?

			jsonArray.add(getObject(o));
		}
		return jsonArray;
	}


	private JsonArray getNumberArray(Number[] currentObject) throws IllegalAccessException
	{
		JsonArray jsonArray = new JsonArray();

		for (Number o : currentObject)
		{
			if (o == null)
				jsonArray.add(JsonNull.INSTANCE);
			else
				jsonArray.add(new JsonPrimitive(o));
		}

		return jsonArray;
	}


	private JsonArray getNumberArray(int[] currentObject) throws IllegalAccessException
	{
		JsonArray jsonArray = new JsonArray();

		for (int o : currentObject)
			jsonArray.add(new JsonPrimitive(o));

		return jsonArray;
	}

	private JsonArray getNumberArray(long[] currentObject) throws IllegalAccessException
	{
		JsonArray jsonArray = new JsonArray();

		for (long o : currentObject)
			jsonArray.add(new JsonPrimitive(o));

		return jsonArray;
	}


	private JsonArray getNumberArray(byte[] currentObject) throws IllegalAccessException
	{
		JsonArray jsonArray = new JsonArray();

		for (byte o : currentObject)
			jsonArray.add(new JsonPrimitive(o));

		return jsonArray;
	}

	private JsonArray getNumberArray(short[] currentObject) throws IllegalAccessException
	{
		JsonArray jsonArray = new JsonArray();

		for (long o : currentObject)
			jsonArray.add(new JsonPrimitive(o));

		return jsonArray;
	}

	private JsonArray getNumberArray(float[] currentObject) throws IllegalAccessException
	{
		JsonArray jsonArray = new JsonArray();

		for (float o : currentObject)
			jsonArray.add(new JsonPrimitive(o));

		return jsonArray;
	}

	private JsonArray getNumberArray(double[] currentObject) throws IllegalAccessException
	{
		JsonArray jsonArray = new JsonArray();

		for (double o : currentObject)
			jsonArray.add(new JsonPrimitive(o));

		return jsonArray;
	}

	public<T> void addListSerialier(Class<T> tClass, ListSerializer<T> locationListSerializer)
	{
		listSerializers.put(tClass, locationListSerializer);
	}

	public<T> void addArraySerialier(Class<T> tClass, ArraySerializer<T> locationListSerializer)
	{
		arraySerializers.put(tClass, locationListSerializer);
	}
}
