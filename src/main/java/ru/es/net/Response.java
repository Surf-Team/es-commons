package ru.es.net;

import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import ru.es.util.JSONUtils;

public class Response
{
	public String responseString;
	public int responseCode;

	public<T> T getObjectFromJson(Class<T> tClass)
	{
		return JSONUtils.createObjectFromJson(responseString, tClass);
	}

	public <T>JsonObject getJsonObject()
	{
		return JSONUtils.getJsonObject(responseString);
	}

	public boolean isOK()
	{
		return responseCode == HttpStatus.SC_OK;
	}
}
