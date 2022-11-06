package ru.es.models.cache;

import ru.es.lang.Value;
import ru.es.log.Log;
import ru.es.models.exceptions.ForbiddenException;
import ru.es.util.HtmlUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FileTemplateManager implements ITemplateManager
{
	public final FileCacheManager fileCache;
	public final FileCacheSettings settings;
	private final List<File> htmlRoot = new ArrayList<>();
	private boolean allowHtmlFileTag = false;

	public FileTemplateManager(FileCacheSettings settings,
							   File htmlRoot)
	{
		this.settings = settings;
		this.htmlRoot.add(htmlRoot);

		fileCache = new FileCacheManager(settings.name,
				settings.fileCacheMaxFileSize,
				settings.fileCacheMaxFilesCount,
				settings.fileCacheLimitRam);
	}

	public void addRoot(File file)
	{
		htmlRoot.add(file);
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
		return processIncludeFile(text, this, null, null);
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


		return bytes;
	}

	public File getFile(String file) throws ForbiddenException, IOException
	{
		File f = null;
		for (File root : htmlRoot)
		{
			f = new File(root, file);

			if (!f.exists())
				continue;

			if (!f.getCanonicalFile().toString().startsWith(root.getCanonicalFile().toString()))
			{
				Log.warning("forbidden canonical: " + f.getCanonicalFile());
				throw new ForbiddenException(f);
			}

			return f;
		}

		// возвращает последний файл из цикла, даже если файл не существует
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


	private static<T> String processIncludeFile(String text, FileTemplateManager fileTemplateManager,
											   OnIncludeHtmlEvent<T> onIncludeHtmlEvent, T object)
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
					String fileContents = fileTemplateManager.readStaticFile(value.get());

					if (onIncludeHtmlEvent != null)
						fileContents = onIncludeHtmlEvent.onEvent(fileContents, object);

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


}
