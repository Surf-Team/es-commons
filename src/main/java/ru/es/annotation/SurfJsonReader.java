package ru.es.annotation;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.*;
import ru.es.log.Log;
import ru.es.reflection.ReflectionUtils;

import java.io.StringReader;
import java.lang.reflect.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class SurfJsonReader
{
	public final Gson gson;
	private final DependencyManager dependencyManager;
	public final Map<Class, ListDeserializer> listDeserializers = new HashMap<>();
	public final Map<Class, ArrayDeserializer> arrayDeserializers = new HashMap<>();

	public SurfJsonReader(DependencyManager dependencyManager)
	{
		this.dependencyManager = dependencyManager;
		gson = new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.disableHtmlEscaping()
				.create();
	}

	public SurfJsonReader()
	{
		this(null);
	}

	public<T> List<T> getCollection(Class<T> tClass, URL url) throws Exception
	{
		var stream = url.openStream();
		var bytes = stream.readAllBytes();
		String serialized = new String(bytes, StandardCharsets.UTF_8);
		return getCollection(tClass, serialized);
	}

	public<T> List<T> getCollection(Class<T> tClass, String data) throws Exception
	{
		JsonElement element = JsonParser.parseReader(new StringReader(data));

		JsonArray array = (JsonArray) element.getAsJsonArray();

		return fillList(array, tClass);
	}

	private <T> List<T> fillList(JsonArray array, Class<T> tClass) throws Exception
	{
		List<T> ret = new ArrayList<>();
		for (int i = 0; i < array.size(); i++)
		{
			JsonElement e = array.get(i);
			if (e.isJsonObject())
			{
				JsonObject jsonObject = e.getAsJsonObject();
				T object = parseObject(jsonObject, tClass, null, null);
				ret.add(object);
			}
			else if (e.isJsonPrimitive())
			{
				T object = (T) parseValue(tClass, e.getAsString());
				ret.add(object);
			}
			else
				throw new Exception("Unknown json type: "+e.toString());
		}
		return ret;
	}


	public  <T> T parseObject(Class<T> tClass, String data) throws Exception
	{
		JsonElement element = JsonParser.parseReader(new StringReader(data));
		JsonObject jsonObject = element.getAsJsonObject();
		T object = parseObject(jsonObject, tClass, null, null);
		return object;
	}


	protected <T> T parseObject(JsonObject jsonObject, Class<T> baseClass, Type[] parametrizedTypes, Object parent) throws Exception
	{
		JsonElement savedClassName = jsonObject.get("__class__");

		T object = null;

		if (savedClassName == null)
			object = baseClass.getConstructor().newInstance();
		else
		{
			var tClass = Class.forName(savedClassName.getAsString());
			if (tClass == null)
			{
				throw new Exception("Could not find class " + savedClassName.getAsString());
			}
			baseClass = (Class<T>) tClass;
			object = (T) tClass.getConstructor().newInstance();
		}

		fillObject(object, jsonObject, baseClass, parametrizedTypes, parent);

		return object;
	}

		// parametrizedTypes это когда залезаем в класс, у которого есть параметры (Класс<параметр1, параметр2>
	// parametrizedTypes = null во всех остальных случаяъ
	public void fillObject(Object object, JsonObject jsonObject, Class baseClass, Type[] parametrizedTypes, Object parent) throws Exception
	{
		for (Class tClass : ReflectionUtils.getClassHierarchy(baseClass))
		{
			for (Field f : tClass.getDeclaredFields())
			{
				try
				{
					if (Modifier.isStatic(f.getModifiers()))
						continue;
					if (Modifier.isPrivate(f.getModifiers()))
						continue;
					if (Modifier.isTransient(f.getModifiers()))
						continue;

					if (f.getAnnotation(Parent.class) != null)
					{
						f.set(object, parent);
						continue;
					}

					if (f.getAnnotation(JsonIgnore.class) != null)
						continue;

					XmlParseSettings parseSettings = f.getAnnotation(XmlParseSettings.class);

					String fieldName = f.getName();
					Class<?> fieldType = f.getType();


					if (parseSettings != null && !parseSettings.readAs().isEmpty())
					{
						// считываем поле, которое не равно названию поля в java
						fieldName = parseSettings.readAs();
					}

					JsonElement jsonValue = jsonObject.get(fieldName);

					if (jsonValue == null)
					{
						if (parseSettings != null && parseSettings.allowDefaultValue())
							continue;
						else if (fieldType == String.class)
							continue; // allow null strings
						else
							throw new Exception(baseClass.getSimpleName() + ", field " + fieldName + ", fieldType: " + fieldType.getSimpleName() + ". Json value not found!");
					}

					if (fieldType.isPrimitive())
					{
						setPrimitiveField(object, f, jsonValue);
					}
					else if (jsonValue.isJsonNull())
					{
						f.set(object, null);
					}
					else if (fieldType.isEnum())
					{
						Object o = Enum.valueOf((Class) fieldType, jsonValue.getAsString());
						f.set(object, o);
					}
					else if (fieldType == String.class)
					{
						f.set(object, jsonValue.getAsString());
					}
					else if (fieldType.isArray())
					{
						var objectType = fieldType.getComponentType();

						var deserializer = arrayDeserializers.get(objectType);

						if (deserializer != null)
							f.set(object, deserializer.deserialize(jsonValue));
						else
						{
							var jsonArray = jsonValue.getAsJsonArray();
							var array = parseArray(jsonArray, objectType, parametrizedTypes, f, object);
							f.set(object, array);
						}
					}
					else if (Collection.class.isAssignableFrom(fieldType)) // collections
					{
						Type genericType = f.getGenericType();
						//Log.warning("field class generic interface type: "+type); // example: java.util.List<java.lang.Integer>

						if (genericType instanceof ParameterizedType)
						{
							ParameterizedType paramType = (ParameterizedType) genericType;
							Type[] typeArguments = paramType.getActualTypeArguments(); // Получение аргументов типа

							if (typeArguments.length > 0)
							{
								Type parametrizedType = typeArguments[0]; // Получение первого аргумента типа

								var deserializer = listDeserializers.get(parametrizedType);

								if (deserializer != null)
									f.set(object, deserializer.deserialize(jsonValue));
								else
								{
									//List list = new ArrayList();
									Class<?> listClass = ArrayList.class;

									if (fieldType == Set.class)
									{
										throw new Exception("Нужно явно указать конкретный тип Set " + tClass.getSimpleName() + ". " + f.getName() + "!");
									}
									else if (fieldType != List.class)
									{
										listClass = fieldType;
										//Log.warning("Use special map class: "+mapClass);
									}

									List list = (List) listClass.getConstructor().newInstance();

									var jsonArray = jsonValue.getAsJsonArray();
									int arraySize = jsonArray.size();

									for (int i = 0; i < arraySize; i++)
									{
										var value = jsonArray.get(i);

										if (parametrizedType == Integer.class)
											list.add(value.getAsInt());
										else if (parametrizedType == Long.class)
											list.add(value.getAsLong());
										else if (parametrizedType == Short.class)
											list.add(value.getAsShort());
										else if (parametrizedType == Byte.class)
											list.add(value.getAsByte());
										else if (parametrizedType == Float.class)
											list.add(value.getAsFloat());
										else if (parametrizedType == Double.class)
											list.add(value.getAsDouble());
										else if (parametrizedType == Boolean.class)
											list.add(value.getAsBoolean());
										else if (parametrizedType == String.class)
											list.add(value.getAsString());
										else
										{
											// проверяем - являются ли объекты в списке залинкованными
											boolean isLink = isLink((Class) parametrizedType);
											if (isLink)
											{
												Class paramClass = (Class) parametrizedType;
												// цепляем по ссылке
												var keyType = AnnotatedUtils.getKeyType(paramClass);

												if (keyType == int.class)
													list.add(dependencyManager.getValue(paramClass, value.getAsInt()));
												else if (keyType == String.class)
													list.add(dependencyManager.getValue(paramClass, value.getAsString()));
												else if (keyType == Long.class)
													list.add(dependencyManager.getValue(paramClass, value.getAsLong()));
												else
													throw new Exception("not done key type in list: " + f.getType());
											}
											else
												list.add(parseObject(value.getAsJsonObject(), (Class) parametrizedType, null, object));
										}
									}
									f.set(object, list);
								}

								// example:  java.lang.Integer
								//System.out.println("parametrizedType: " + parametrizedType);
							}
							else
								throw new Exception("Generic parameters size = 0 for list " + tClass.getSimpleName() + ". " + f.getName() + "!");
						}
						else
							throw new Exception("Generic is not ParameterizedType for list " + tClass.getSimpleName() + ". " + f.getName() + "!");

					}
					else if (Map.class.isAssignableFrom(fieldType)) // collections
					{
						Type genericType = f.getGenericType();
						//Log.warning("field class generic interface type: "+type); // example: java.util.List<java.lang.Integer>

						if (genericType instanceof ParameterizedType)
						{
							ParameterizedType paramType = (ParameterizedType) genericType;
							Type[] typeArguments = paramType.getActualTypeArguments(); // Получение аргументов типа

							if (typeArguments.length > 0)
							{
								// тут может быть ошибка, если typeArgument является parametrized type, но мы пока такое не поддерживаем
								Class parametrizedType1 = (Class) typeArguments[0];


								var mapJsonObject = jsonValue.getAsJsonObject();

								Class<?> mapClass = HashMap.class;

								if (fieldType != Map.class)
								{
									mapClass = fieldType;
									//Log.warning("Use special map class: "+mapClass);
								}

								Map map = (Map) mapClass.getConstructor().newInstance();

								for (var e : mapJsonObject.entrySet())
								{
									var key = parseValue(parametrizedType1, e.getKey());

									if (typeArguments[1] instanceof Class)
									{
										Class parametrizedType2 = (Class) typeArguments[1];
										if (e.getValue() == JsonNull.INSTANCE)
											map.put(key, null);
										else if (parametrizedType2 == String.class)
										{
											map.put(key, e.getValue().getAsString());
										}
										else if (isNumberClass(parametrizedType2))
										{
											map.put(key, parseValue(parametrizedType2, e.getValue().getAsString()));
										}
										else
										{
											if (parametrizedType2.isArray())
											{
												var deserializer = arrayDeserializers.get(parametrizedType2.getComponentType());

												if (deserializer != null)
												{
													map.put(key, deserializer.deserialize(e.getValue()));
												}
												else
												{
													var array = parseArray(e.getValue().getAsJsonArray(), parametrizedType2.getComponentType(),
															parametrizedTypes, f, object);
													map.put(key, array);
												}
											}
											else
											{
												if (e.getValue().isJsonObject())
													map.put(key, parseObject(e.getValue().getAsJsonObject(), parametrizedType2, null, object));
												else if (e.getValue().isJsonPrimitive())
													map.put(key, parseValue(parametrizedType2, e.getValue().toString()));
												else
													throw new Exception("Not done parser (Map<..., NotDoneType>");
											}
										}
									}
									// например это может быть List<String> внутри Map value, то есть Map<Integer, List<String>>
									// в этом месте мы разбираем именно List<String>
									else if (typeArguments[1] instanceof ParameterizedType)
									{
										var typeArg1 = (ParameterizedType) typeArguments[1];

										if (typeArg1.getActualTypeArguments().length > 1)
											throw new Exception("Not done parsing for > 1 type arguments in parametrized type (Map in Map value?)");

										// в случае с List<String> это List
										var rawType = typeArg1.getRawType();

										if (rawType == List.class)
										{
											// внутри List<String> это String
											var typeArg1_1 = typeArg1.getActualTypeArguments()[0];

											var collectionInValue = fillList(e.getValue().getAsJsonArray(), (Class) typeArg1_1);
											map.put(key, collectionInValue);
										}
										else
										{
											throw new Exception("Not done. Allowed only list types in map value");
										}
									}
									else
										throw new Exception("Not done parser "+typeArguments[1]);
								}
								f.set(object, map);
							}
							else
								throw new Exception("Generic parameters size = 0 for map " + tClass.getSimpleName() + ". " + f.getName() + "!");
						}
						else
							throw new Exception("Generic is not ParameterizedType for map " + tClass.getSimpleName() + ". " + f.getName() + "!");
					}
					else
					{
						if (f.getAnnotation(JsonIdentityReference.class) != null)
						{
							// цепляем по ссылке
							var keyType = AnnotatedUtils.getKeyType(f.getType());

							if (keyType == int.class)
								f.set(object, dependencyManager.getValue(f.getType(), jsonValue.getAsInt()));
							else if (keyType == String.class)
								f.set(object, dependencyManager.getValue(f.getType(), jsonValue.getAsString()));
							else if (keyType == Long.class)
								f.set(object, dependencyManager.getValue(f.getType(), jsonValue.getAsLong()));
							else
								throw new Exception("not done key type: " + f.getType());
						}
						else
						{
							Type genericTypez = f.getGenericType();

							Type[] typeArguments = null;
							if (genericTypez instanceof ParameterizedType)
							{
								ParameterizedType paramType = (ParameterizedType) genericTypez;
								typeArguments = paramType.getActualTypeArguments(); // Получение аргументов типа <..., ...>
							}

							f.set(object, parseObject(jsonValue.getAsJsonObject(), fieldType, typeArguments, object));
						}
					}
				}
				catch (Exception e)
				{
					Log.warning("SurfJsonReader: error when parse class "+tClass.getSimpleName()+", field "+f.getName()+", type: "+f.getType().getSimpleName());
					throw e;
				}
			}
		}
	}

	private Object parseArray(JsonArray jsonArray, Class objectType, Type[] parametrizedTypes, Field f, Object parent) throws Exception
	{
		int arraySize = jsonArray.size();

		Object array = Array.newInstance(objectType, arraySize);

		if (arraySize > 0)
		{
			if (objectType.isPrimitive())
			{
				fillArray(jsonArray, objectType, array);
			}
			else if (isNumberClass(objectType) || objectType == Boolean.class)
			{
				for (int i = 0; i < arraySize; i++)
				{
					var element = jsonArray.get(i);

					if (element == JsonNull.INSTANCE)
						Array.set(array, i, null);
					else
						Array.set(array, i, parseValue(objectType, element.getAsString()));
				}
			}
			else if (objectType == String.class)
			{
				for (int i = 0; i < arraySize; i++)
				{
					var element = jsonArray.get(i);

					Array.set(array, i, element.getAsString());
				}
			}
			else
			{
				Type fieldGenericType = f.getGenericType();

				// Проверка, является ли тип параметризованным массивом (T[])
				if (fieldGenericType instanceof GenericArrayType)
				{
					if (parametrizedTypes == null || parametrizedTypes.length == 0)
						throw new Exception("Параметризированный тип T[] не может быть в классе, у которого нет параметров");

					// тут может быть недоработка^
					// если у класса только 1 пар.тип, то всё ок
					// если же у класса несколько пар.типов, то могут быть проблемы, например класс Class<A,B>
					// если массив A[] то будет всё ок = parametrizedTypes[0]
					// если массив B[] то будут проблемы = parametrizedTypes[1]
					var arrayParameter = (Class) parametrizedTypes[0]; // A[]

					if (arrayParameter.isPrimitive())
						fillArray(jsonArray, objectType, array);
					else
					{
						for (int i = 0; i < arraySize; i++)
						{
							var element = jsonArray.get(i);

							if (element == JsonNull.INSTANCE)
								Array.set(array, i, null);
							else if (isNumberClass(arrayParameter) || arrayParameter == Boolean.class)
								Array.set(array, i, parseValue(arrayParameter, element.getAsString()));
							else
								Array.set(array, i, parseObject(element.getAsJsonObject(), arrayParameter, null, parent));
						}
					}
				}
				else
				{
					for (int i = 0; i < arraySize; i++)
					{
						var element = jsonArray.get(i);
						Array.set(array, i, parseObject(element.getAsJsonObject(), objectType, null, parent));
					}
				}
			}
		}
		return array;
	}

	private void fillArray(JsonArray jsonArray, Class<?> objectType, Object array) throws Exception
	{
		int arraySize = jsonArray.size();

		for (int i = 0; i < arraySize; i++)
		{
			var element = jsonArray.get(i);

			if (objectType == int.class)
				Array.set(array, i, element.getAsInt());
			else if (objectType == short.class)
				Array.set(array, i, element.getAsShort());
			else if (objectType == long.class)
				Array.set(array, i, element.getAsLong());
			else if (objectType == byte.class)
				Array.set(array, i, element.getAsByte());
			else if (objectType == float.class)
				Array.set(array, i, element.getAsFloat());
			else if (objectType == double.class)
				Array.set(array, i, element.getAsDouble());
			else if (objectType == boolean.class)
				Array.set(array, i, element.getAsBoolean());
			else
				throw new Exception("Unknown primitive array type!");
		}
	}


	private boolean isLink(Class<?> collectedClass)
	{
		return dependencyManager.getCollectionClasses().contains(collectedClass);
	}

	private boolean isNumberClass(Class<?> type)
	{
		return type == int.class || type == double.class || type == float.class || type == short.class || type == long.class || type == byte.class ||
				type == Integer.class || type == Double.class || type == Float.class || type == Short.class || type == Long.class || type == Byte.class;
	}

	private <T> void setPrimitiveField(T object, Field f, JsonElement valueElement) throws Exception
	{
		Class<?> fieldType = f.getType();

		if (fieldType == int.class)
			f.setInt(object, valueElement.getAsInt());
		else if (fieldType == double.class)
			f.setDouble(object, valueElement.getAsDouble());
		else if (fieldType == float.class)
			f.setFloat(object, valueElement.getAsFloat());
		else if (fieldType == long.class)
			f.setLong(object, valueElement.getAsLong());
		else if (fieldType == short.class)
			f.setShort(object, valueElement.getAsShort());
		else if (fieldType == byte.class)
			f.setByte(object, valueElement.getAsByte());
		else if (fieldType == boolean.class)
			f.setBoolean(object, valueElement.getAsBoolean());
	}



	public static Object parseValue(Class type, String value) throws Exception
	{
		if (value == null || value.equals("null"))
		{
			if (type == boolean.class)
				return Boolean.FALSE;
			else if (type == long.class)
				return 0L;
			else if (type == int.class)
				return 0;
			else if (type == byte.class)
				return (byte) 0;
			else if (type == short.class)
				return (short) 0;
			else if (type == float.class)
				return 0.0f;
			else if (type == double.class)
				return 0.0;
		}

		if (value == null && type == String.class)
			return null;

		if (type == boolean.class || type == Boolean.class)
			return Boolean.parseBoolean(value);
		else if (type == String.class)
			return value;
		else if (type == int.class || type == Integer.class)
			return Integer.parseInt(value);
		else if (type == long.class || type == Long.class)
			return Long.parseLong(value);
		else if (type == float.class || type == Float.class)
			return Float.parseFloat(value);
		else if (type == double.class || type == Double.class)
			return Double.parseDouble(value);
		else if (type == short.class || type == Short.class)
			return Short.parseShort(value);
		else if (type == byte.class || type == Byte.class)
			return Byte.parseByte(value);

		throw new Exception("Unknon primitive type: "+type.getSimpleName());
	}

	public<T> void addListDeserializer(Class<T> tClass, ListDeserializer<T> deserializer)
	{
		listDeserializers.put(tClass, deserializer);
	}
	public<T> void addArrayDeserialier(Class<T> tClass, ArrayDeserializer<T> deserializer)
	{
		arrayDeserializers.put(tClass, deserializer);
	}
}
