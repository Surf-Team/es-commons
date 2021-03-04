package ru.es.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ru.es.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JSONUtils
{
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
}
