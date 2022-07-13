package ru.es.models.cache;

import ru.es.lang.Value;
import ru.es.log.Log;
import ru.es.models.exceptions.ForbiddenException;
import ru.es.util.HtmlUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

public class FileTemplateManager implements ITemplateManager
{
	public final FileCacheManager fileCache;
	public final FileCacheSettings settings;
	private final File htmlRoot;
	private boolean allowHtmlFileTag = false;

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
		return readStaticFile(getFile(f));
	}

	// более новая версия
	public String read(String f, int lang) throws Exception
	{
		String ret = readStaticFile(getFile(f));
		ret = HtmlUtils.replaceLangTag(ret, lang);
		return ret;
	}

	// более новая версия
	public String readNoExc(String f, int lang)
	{
		try
		{
			String ret = readStaticFile(getFile(f));
			ret = HtmlUtils.replaceLangTag(ret, lang);
			return ret;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "Template not found";
		}
	}

	public String readStaticFile(File f) throws IOException
	{
		String ret = new String(readStaticFileB(f));

		if (allowHtmlFileTag)
		{
			ret = processIncludeFile(ret);
		}

		return ret;
	}

	private String processIncludeFile(String text)
	{
		String startTag = "htmlfile(";
		String endTag = ")";

		int deadlock = 0;

		Value<String> data = new Value<>(text);
		while (true)
		{
			boolean replaced = HtmlUtils.replaceTag(data, startTag, endTag, value -> {
				try
				{
					String fileContents = readStaticFile(value.get());
					value.set(fileContents);
					return true;
				}
				catch (Exception e)
				{
					Log.warning("Error in htmlfile tag");
					e.printStackTrace();
				}
				return false;
			});

			if (!replaced)
				break;

			deadlock++;

			if (deadlock > 1000)
			{
				Log.warning("processHtmlInclude: deadlock detected for tag: "+startTag+" "+endTag);
				break;
			}
		}

		return data.get();
	}

	public byte[] readStaticFileB(File f) throws IOException
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

	public void allowFileTag()
	{
		allowHtmlFileTag = true;
	}




}
