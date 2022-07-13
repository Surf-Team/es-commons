package ru.es.util.writers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import ru.es.log.Log;

import java.io.IOException;
import java.net.URL;

public class HttpUrlWriter implements UrlByteWriter
{
	@Override
	public void write(URL url, byte[] bytes) throws IOException
	{
		String path = url.getPath();
		String file = url.getFile();
		String host = url.getHost();
		int port = url.getPort();
		String protocol = url.getProtocol();
		Log.warning("path: "+path);
		Log.warning("file: "+file);
		Log.warning("host: "+host);
		Log.warning("port: "+port);
		Log.warning("protocol: "+protocol);
		String pathToFile = file.substring(1, file.lastIndexOf("/")+1);

		String uploadScriptFile = "upload.php";
		String scriptURL = protocol+"://"+host+":"+port+"/"+uploadScriptFile;

		HttpClient httpClient = HttpClientBuilder.create().build();
		//HttpPut putRequest = new HttpPut(scriptURL);
		HttpPost request = new HttpPost(scriptURL);
		//request.addHeader("Content-Type", "multipart/form-data");
		//request.addHeader("Content-Length", bytes.length+""); добавляется сам

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("path", pathToFile);
		builder.addBinaryBody("userfile", bytes, ContentType.APPLICATION_OCTET_STREAM, file);
		// fileParamName should be replaced with parameter name your REST API expect.
		//builder.addPart("fileParamName", new FileBody(file));
		//builder.addPart("optionalParam", new StringBody("true", ContentType.create("text/plain", Consts.ASCII)));
		request.setEntity(builder.build());

		HttpResponse response = httpClient.execute(request);

		HttpEntity entity = response.getEntity();
		String result = EntityUtils.toString(entity);
		Log.warning(result);
		EntityUtils.consume(entity);
	}
}
