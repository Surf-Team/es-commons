package ru.es.annotation;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Collection;

public interface ListDeserializer<T>
{
	Collection<T> deserialize(JsonElement jsonValue) throws Exception;
}
