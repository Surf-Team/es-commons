package ru.es.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class JSONUtils
{
    public static JsonObject getJsonObject(File f) throws FileNotFoundException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(f));

        JsonElement element = JsonParser.parseReader(bufferedReader);

        return element.getAsJsonObject();
    }

}
