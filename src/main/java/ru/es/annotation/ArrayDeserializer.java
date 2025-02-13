package ru.es.annotation;

import com.google.gson.JsonElement;

import java.util.Collection;

public interface ArrayDeserializer<T>
{
	T[] deserialize(JsonElement jsonValue) throws Exception;
}
