package ru.es.annotation;

import com.google.gson.JsonElement;

import java.io.IOException;

public interface ArraySerializer<T>
{
	JsonElement serialize(T[] value) throws IOException;
}
