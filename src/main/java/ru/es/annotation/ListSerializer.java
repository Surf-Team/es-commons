package ru.es.annotation;

import com.google.gson.JsonElement;

import java.io.IOException;
import java.util.Collection;

public interface ListSerializer<T>
{
	JsonElement serialize(Collection<T> value) throws IOException;
}
