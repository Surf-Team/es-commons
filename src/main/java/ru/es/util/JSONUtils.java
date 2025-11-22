package ru.es.util;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.google.gson.*;
import ru.es.log.Log;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSONUtils
{
    private static Gson gson = new Gson();
    public static Gson prettyGson = JSONUtils.gson.newBuilder().setPrettyPrinting().create();

    public static void main(String[] args)
    {
        int arr = 0;
        for (List<String> s : parseListOfList("{{2395;3533};{2419};{5775};{5787}}", ";"))
        {
            arr++;
            for (String ss : s)
            {
                Log.warning(arr+" "+ss);
            }
        }         
    }

    public static JsonObject getJsonObject(File f) throws FileNotFoundException
    {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(f));

        JsonElement element = JsonParser.parseReader(bufferedReader);

        return element.getAsJsonObject();
    }

    // convert string:
    // ["a", "b"]
    // to list
    public static List<String> jsonToStringList(String f)
    {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(f));

        JsonElement element = JsonParser.parseReader(bufferedReader);

        List<String> ret = new ArrayList<>();
        for (var a : element.getAsJsonArray())
        {
            ret.add(a.getAsString());
        }
        return ret;
    }

    public static JsonObject getJsonObject(String f)
    {
        JsonElement element = JsonParser.parseString(f);

        return element.getAsJsonObject();
    }

    public static JsonObject objectToJsonTree(Object object)
    {
        JsonElement element = getJsonObject(toJsonString(object));

        return element.getAsJsonObject();
    }

    // {{2395;3533};{2419};{5775};{5787}}
    public static List<List<String>> parseListOfList(String data, String delim)
    {
        List<List<String>> ret = new ArrayList<>();
        // {{2395;3533};{2419};{5775};{5787}}
        data = data.substring(1, data.length()-1);
        // {2395;3533};{2419};{5775};{5787}
        int cursor = 0;
        while (true)
        {
            int endBlock = data.indexOf("}", cursor);
            if (endBlock == -1)
                break;


            // {2395;3533} -> 2395;3533
            String blockData = data.substring(cursor+1, endBlock);
            String[] datas = blockData.split( delim);
            List<String> dataList = new ArrayList<>(Arrays.asList(datas));
            ret.add(dataList);

            cursor = endBlock+2;
        }
        return ret;
    }

    // {13451;3535};
    public static<T> String toList(List<T> list, String delim)
    {
        StringBuilder ret = new StringBuilder();

        ret.append("{");
        boolean first = true;
        for (T o : list)
        {
            if (!first)
                ret.append(delim);
            else
                first = false;

            ret.append(o);
        }
        ret.append("}");

        return ret.toString();
    }

    public static String toJsonString(Object object)
    {
        return gson.toJson(object);
    }

    public static String toJsonStringPretty(Object object)
    {
        return prettyGson.toJson(object);
    }

    public static String jsonElementToString(JsonElement object)
    {
        return prettyGson.toJson(object);
    }

    public static<T> T createObjectFromJson(String jsonString, Class<T> object) throws JsonSyntaxException
    {
        return gson.fromJson(jsonString, object);
    }

    public static<T> T getJsonFromURL(URL jsonFileUrl, Class<T> tClass) throws IOException
    {
        byte[] content = jsonFileUrl.openStream().readAllBytes();
        String parsed = new String(content, StandardCharsets.UTF_8);
        ObjectMapper mapper = new JsonMapper();
        T ret = mapper.readValue(parsed, tClass);
        return ret;
    }



    public static void write(File file, Object o) throws IOException
    {
        var writer = getDefaultWriter();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer.writeValue(byteArrayOutputStream, o);
        byte[] array = byteArrayOutputStream.toByteArray();

        // if save
        FileUtils.writeFile(file, array);
    }

    private static ObjectWriter getDefaultWriter()
    {
        ObjectMapper objectMapper = JsonMapper.builder()
                .enable(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .build();

        DefaultPrettyPrinter.Indenter indenter =
                new DefaultIndenter("    ", DefaultIndenter.SYS_LF);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith(indenter);
        printer.indentArraysWith(indenter);


        return  objectMapper.writer(printer);
    }

    public static void write(URL file, Object o) throws IOException
    {
        var writer = getDefaultWriter();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        writer.writeValue(byteArrayOutputStream, o);
        byte[] array = byteArrayOutputStream.toByteArray();

        // if save
        FileUtils.writeToURL(file, array);
    }

    public static<T> T load(URL url, Class<T> tClass) throws IOException
    {
        ObjectMapper deSerializeMapper = new ObjectMapper();
        var reader = deSerializeMapper.reader();

        return load(url, tClass, deSerializeMapper);
    }

    public static<T> T load(URL url, Class<T> tClass, ObjectMapper objectMapper) throws IOException
    {
        var reader = objectMapper.reader();

        var stream = url.openStream();
        var bytes = stream.readAllBytes();
        String serialized = new String(bytes, StandardCharsets.UTF_8);

        T ret = (T) reader.readValue(serialized, tClass);
        return ret;
    }

    public static int getProperty(JsonObject jsonObject, String name, int defaultVal)
    {
        var ret = jsonObject.get(name);
        try
        {
            return Integer.parseInt(ret.getAsString());
        }
        catch (Exception e)
        {
            return defaultVal;
        }
    }

    public static String getProperty(JsonObject jsonObject, String name, String defaultVal)
    {
        var ret = jsonObject.get(name);
        try
        {
            return ret.getAsString();
        }
        catch (Exception e)
        {
            return defaultVal;
        }
    }
}
