package ru.es.fileCache;

import ru.es.log.Log;
import ru.es.exceptions.ForbiddenException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class TemplateManager implements ITemplateManager
{
	public final FileCacheManager fileCache;
	public final FileCacheSettings settings;
	private final URL htmlRoot;

	public TemplateManager(FileCacheSettings settings,
						   URL htmlRoot)
	{
		this.settings = settings;
		this.htmlRoot = htmlRoot;

		fileCache = new FileCacheManager(settings.name,
				settings.fileCacheMaxFileSize,
				settings.fileCacheMaxFilesCount,
				settings.fileCacheLimitRam);
	}


	private void checkSecure(String f) throws ForbiddenException
	{
		if (f.contains("./"))
			throw new ForbiddenException(f);

		if (f.endsWith("/"))
			throw new ForbiddenException(f);
	}


	public String readStaticFile(String f) throws Exception
	{
		checkSecure(f);
		return new String(readStaticFile(getFile(f)));
	}

	public String read(String f)
	{
		if (f.length() > 256)
			return "Too long file name: "+f;
		
		try
		{
			checkSecure(f);
			return new String(readStaticFile(getFile(f)));
		}
		catch (Exception e)
		{
			Log.warning("File not found: "+f);
			e.printStackTrace();
			return "Not found: "+f;
		}
	}

	public byte[] readStaticFile(URL url) throws IOException
	{
		byte[] bytes = null;
		if (settings.fileCache)
		{
			bytes = fileCache.get(url);
			//Log.warning("Used file cache for "+f.getName());
		}

		if (bytes == null)
		{
			try
			{
				InputStream stream = url.openStream();
				bytes = stream.readAllBytes();
				stream.close();

				if (settings.fileCache)
					fileCache.tryAddFile(url, bytes);
			}
			catch (Exception e)
			{
				Log.warning("URL not found: "+url);
				throw e;
			}
		}

		return bytes;
	}

	private URL getFile(String file) throws IOException
	{
		return new URL(htmlRoot+file);
	}
}
