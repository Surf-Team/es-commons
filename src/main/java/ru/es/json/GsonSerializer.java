package ru.es.json;

import com.google.gson.Gson;
import ru.es.log.Log;
import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class GsonSerializer<T>
{
    Gson gson;
    File file;

    public GsonSerializer(File file)
    {
        gson = new Gson();
        this.file = file;
    }

    public T load(Class<T> c)
    {
        try
        {
            String fileContent = FileUtils.readFileDefaultEncode(file);
            T ret = gson.fromJson(fileContent, c);
            if (ret == null)
            {
                Log.warning("Cant load settings. First launch (1)?");
                return c.newInstance();
            }
            Log.warning("Ret is not null");
            return ret;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.warning("Cant load settings. First launch? "+e.getMessage());
        }
        try
        {
            return c.newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public void save(T t) throws IOException
    {
        String save = gson.toJson(t);
        FileUtils.writeFile(file, save);
    }
}
