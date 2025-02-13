package ru.es.net;

import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import ru.es.log.Log;
import ru.es.util.JSONUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 17.11.14
 * Time: 2:58
 * To change this template use File | Settings | File Templates.
 */
public class HttpRequest
{
    public class QueryString {
        private StringBuffer query;

        public QueryString() {
            query = new StringBuffer();
        }

        public synchronized QueryString add(Object name, Object value)
                throws UnsupportedEncodingException
        {
            if (!query.toString().trim().equals("")) query.append("&");
            query.append(URLEncoder.encode(name.toString(), "UTF-8"));
            query.append("=");
            query.append(URLEncoder.encode(value.toString(), "UTF-8"));
            return this;
        }

        public String toString() {
            return query.toString();
        }
    }

    public static String sendRequest(String url) throws IOException
    {
        URLConnection conn = new URL(url).openConnection();

        //conn.setRequestProperty("Referer", "http://ya.ru/");
        //conn.setRequestProperty("Cookie", "test=1");

        String html = readStreamToString(conn.getInputStream(), "UTF-8");
        return html;
    }

    private static String readStreamToString(InputStream in, String encoding)
            throws IOException {
        StringBuffer b = new StringBuffer();
        InputStreamReader r = new InputStreamReader(in, encoding);
        int c;
        while ((c = r.read()) != -1) {
            b.append((char)c);
        }
        return b.toString();
    }


    public static String httpGet(String url) throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setConnectTimeout(2000);
        con.setReadTimeout(2000);

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = con.getResponseCode();
        //System.out.println("\nSending 'GET' request to URL : " + url);
        //System.out.println("Response Code : " + responseCode);

        var is = con.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        is.close();
        con.disconnect();

        return  response.toString();
    }

    public static String httpPost(String url, String urlParameters) throws Exception
    {
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        //System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    //        String urlParameters =
    //"fName=" + URLEncoder.encode("???", "UTF-8") +
//            "&lName=" + URLEncoder.encode("???", "UTF-8")
    public static String excutePost(String targetURL, String urlParameters)
    {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(6000);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            /**InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }  **/

            //return readStreamToString(connection.getInputStream(), "UTF-8");
            return readStreamToString(connection.getInputStream(), "windows-1251");


            //rd.close();
            //return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }


    public static byte[] httpPostBase64Data(String url, String parameters, byte[] data) throws Exception
    {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        Log.warning("Sending 'POST' request to URL : " + url);

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.write(parameters.getBytes(StandardCharsets.UTF_8));
        wr.write("\r\n".getBytes(StandardCharsets.UTF_8));
        wr.write(Base64.getEncoder().encode(data));
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        Log.warning("Response Code : " + responseCode);

        if (responseCode != 200)
            throw new ServerException("Server error! Response code: "+responseCode);


        InputStream inputStream = con.getInputStream();

        byte[] response = inputStream.readAllBytes();
        //Log.warning("response bytes: "+response.length);

        return response;
    }



    public static <Request> Response jsonPost(String href, Request request) throws IOException
    {
        Map<String, String> headers = new HashMap<>();
        return jsonPost(href, request, headers);
    }

    public static <Request> Response jsonPost(String href, Request request, Map<String, String> headers) throws IOException
    {
        String jsonString = JSONUtils.toJsonString(request);
        return  jsonPostString(href, jsonString, headers);
    }

    public static Response jsonPostJson(String href, JsonObject request, Map<String, String> headers) throws IOException
    {
        return jsonPostString(href, request.toString(), headers);
    }

    public static Response jsonPostString(String href, String data, Map<String, String> headers) throws IOException
    {
        HttpClientBuilder builder = HttpClientBuilder.create();

        var requestConfig = RequestConfig.custom();
        requestConfig.setConnectionRequestTimeout(3000);
        requestConfig.setConnectTimeout(3000);

        builder.setDefaultRequestConfig(requestConfig.build());

        HttpClient httpClient = builder.build();

        HttpPost httpPostRequest = new HttpPost(href);
        httpPostRequest.addHeader("Content-Type", "application/json");
        for (String s : headers.keySet())
        {
            httpPostRequest.addHeader(s, headers.get(s));
        }


        StringEntity stringEntity = new StringEntity(data, StandardCharsets.UTF_8);
        httpPostRequest.setEntity(stringEntity);

        HttpResponse response = httpClient.execute(httpPostRequest);

        HttpEntity entity = response.getEntity();

        String responseString = EntityUtils.toString(entity);

        Response ret = new Response();

        ret.responseString = responseString;
        ret.responseCode = response.getStatusLine().getStatusCode();

        EntityUtils.consume(entity);

        return ret;
    }



}
