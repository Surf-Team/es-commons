package ru.es.models.cache;

import ru.es.log.Log;
import ru.es.models.exceptions.ForbiddenException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class FileTemplateManager implements ITemplateManager
{
	public final FileCacheManager fileCache;
	public final FileCacheSettings settings;
	private final File htmlRoot;

	public FileTemplateManager(FileCacheSettings settings,
							   File htmlRoot)
	{
		this.settings = settings;
		this.htmlRoot = htmlRoot;

		fileCache = new FileCacheManager(settings.name,
				settings.fileCacheMaxFileSize,
				settings.fileCacheMaxFilesCount,
				settings.fileCacheLimitRam);
	}

	public String readStaticFile(String f) throws Exception
	{
		return new String(readStaticFile(getFile(f)));
	}

	public byte[] readStaticFile(File f) throws IOException
	{
		URL url = f.toURI().toURL();

		byte[] bytes = null;
		if (settings.fileCache)
		{
			bytes = fileCache.get(url);
			//Log.warning("Used file cache for "+f.getName());
		}

		if (bytes == null)
		{
			if (f.exists())
			{
				FileInputStream fis = new FileInputStream(f);

				bytes = new byte[fis.available()];
				fis.read(bytes);
				fis.close();

				if (settings.fileCache)
					fileCache.tryAddFile(url, bytes);
			}
			else
				return null;
		}

        /*if (f.getName().endsWith(".html"))
        {
            //todo static file???
            bytes = settings.templateEngine.process(exchangeWrapper, bytes);
        } */

		return bytes;
	}

	public File getFile(String file) throws ForbiddenException, IOException
	{
		File f = new File(htmlRoot, file);
		//Log.warning("requested file: "+f);

		if (!f.getCanonicalFile().toString().startsWith(htmlRoot.getCanonicalFile().toString()))
		{
			Log.warning("forbidden canonical: "+f.getCanonicalFile());
			throw new ForbiddenException(f);
		}

		return f;
	}


	public boolean allowCache()
	{
		return settings.fileCache;
	}

}
