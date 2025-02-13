package ru.es.fileCache;

import ru.es.lang.Value;
import ru.es.log.Log;
import ru.es.exceptions.ForbiddenException;
import ru.es.util.HtmlUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileTemplateManager implements ITemplateManager
{
	public final FileCacheManager fileCache;
	public final FileCacheSettings settings;
	public final FileCacheManagerV2[] fileCacheV2;
	private final List<File> htmlRoot = new ArrayList<>();
	private final Map<String, FileInfo> cachedFileObject = new HashMap<>();
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

		fileCacheV2 = new FileCacheManagerV2[] {
				new FileCacheManagerV2(settings.name,
						settings.fileCacheMaxFileSize,
						settings.fileCacheMaxFilesCount,
						settings.fileCacheLimitRam),
				new FileCacheManagerV2(settings.name,
						settings.fileCacheMaxFileSize,
						settings.fileCacheMaxFilesCount,
						settings.fileCacheLimitRam)
		};
	}

	public void addRoot(File file) throws FileNotFoundException
	{
		if (!file.exists())
			throw new FileNotFoundException("File "+file.getAbsolutePath()+" doesnt exist!");
		htmlRoot.add(file);
	}

	public String readStaticFile(String f) throws Exception
	{
		return readStaticFile(getFile(f), null);
	}

	public String read(String f, int lang) throws Exception
	{
		String ret = readStaticFile(getFile(f), null);
		ret = HtmlUtils.replaceLangTag(ret, lang);
		return ret;
	}

	// читает / готовит сразу 2 шаблона под 2 языка
	public String[] read(String file)
	{
		String[] retArr = new String[2];
		try
		{
			for (int lang = 0; lang < 2; lang++)
			{
				String ret = readStaticFile(getFile(file), null);
				ret = HtmlUtils.replaceLangTag(ret, lang);
				retArr[lang] = ret;
			}
		}
		catch (Exception e)
		{
			retArr[0] = "file not found: "+file;
			retArr[1] = "file not found: "+file;
		}
		return retArr;
	}

	// более новая версия
	public String read(String fileName, int lang, IncludeFileEvent onInclude) throws Exception
	{

		String ret = null;

		if (settings.fileCache && fileCacheV2[lang].exists(fileName))
			ret = fileCacheV2[lang].get(fileName);
		else
		{
			File file = getFile(fileName);
			if (file.exists())
			{
				FileInputStream fis = new FileInputStream(file);
				byte[] bytes = fis.readAllBytes();
				fis.close();

				ret = new String(bytes, StandardCharsets.UTF_8);
				ret = HtmlUtils.replaceLangTag(ret, lang);

				if (settings.fileCache)
					fileCacheV2[lang].tryAddFile(fileName, ret);

				//Log.warning("Load new file: " + fileName);
			}
		}

		if (ret == null)
			return null;

		if (allowHtmlFileTag)
		{
			ret = processIncludeFileV2(ret, onInclude, lang);

			if (onInclude != null)
				ret = onInclude.replaceIncludedFile(fileName, ret);
		}

		return ret;
	}

	// более новая версия
	public String readNoExc(String f, int lang)
	{
		try
		{
			String ret = readStaticFile(getFile(f), null);
			ret = HtmlUtils.replaceLangTag(ret, lang);
			return ret;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return "Template not found";
		}
	}


	public String readStaticFile(File f, IncludeFileEvent onInclude) throws IOException
	{
		String ret = new String(readStaticFileB(f));

		if (allowHtmlFileTag)
		{
			ret = processIncludeFile(ret, onInclude);
		}

		return ret;
	}

	private<T> String processIncludeFile(String text, IncludeFileEvent onInclude)
	{
		return processIncludeFile(text, this, onInclude);
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

	public FileInfo getFileV2(String file) throws ForbiddenException, IOException
	{
		if (settings.fileCache)
		{
			FileInfo info = cachedFileObject.get(file);
			if (info != null)
			{
				if (info.forbidden)
					throw new ForbiddenException(info.file);

				return info;
			}
		}

		FileInfo ret = new FileInfo();

		for (File root : htmlRoot)
		{
			ret.file = new File(root, file);

			if (!ret.file.exists())
				continue;

			if (!ret.file.getCanonicalFile().toString().startsWith(root.getCanonicalFile().toString()))
			{
				Log.warning("forbidden canonical: " + ret.file.getCanonicalFile());

				ret.forbidden = true;
			}
			else
			{
				ret.exists = true;
				break;
			}
		}

		if (settings.fileCache)
		{
			cachedFileObject.put(file, ret);
		}

		if (ret.forbidden)
			throw new ForbiddenException(ret.file);

		return ret;
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
			else
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


	private static<T> String processIncludeFile(String text, FileTemplateManager fileTemplateManager, IncludeFileEvent replacer)
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

					if (replacer != null)
						fileContents = replacer.replaceIncludedFile(value.get(), fileContents);

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


	private <T> String processIncludeFileV2(String text, IncludeFileEvent replacer, int lang)
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
					String fileContents = read(value.get(), lang, replacer);

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
