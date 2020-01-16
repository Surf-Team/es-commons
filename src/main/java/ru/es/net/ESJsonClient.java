package ru.es.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.simple.JSONObject;
import ru.es.log.Log;

public class ESJsonClient
{
    public final String url;

    // http://polyform-studio.com/engine.php?json=
    public ESJsonClient(String url)
    {
        this.url = url;
    }

    enum Method
    {
        GET,
        POST
    }

    public String getResponse(JSONObject jsonObject) throws IOException
    {
        return getHttpAnswer(getUrl(jsonObject));
    }

    public String getResponse(JSONObject jsonObject, Method method) throws IOException
    {
        return getHttpAnswer(getUrl(jsonObject));
    }

    private URL getUrl(JSONObject jsonObject)
    {
        try
        {
            String urlDecoded = url+URLEncoder.encode(jsonObject.toJSONString(), "UTF-8");
            return new URL(urlDecoded);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Log.warning("Something wrong");
        return null;
    }

    public String test() throws IOException
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("var1", true);
        jsonObject.put("var2", false);
        jsonObject.put("var3", "Hello World!");

        return getHttpAnswer(getUrl(jsonObject));
    }

    public String getHttpAnswer(URL url) throws IOException
    {
        URLConnection conn = null;
        try
        {
            conn = url.openConnection();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }

        StringBuilder ret = null;
        if (conn != null)
        {
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null)
            {
                if (ret == null)
                {
                    ret = new StringBuilder();
                    ret.append(line);
                }
                else
                {
                    ret.append(System.lineSeparator());
                    ret.append(line);
                }
            }
            rd.close();
        }
        if (ret != null)
            return ret.toString();
        else
            return null;
    }
}
