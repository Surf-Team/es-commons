package ru.es.fileCache;

import ru.es.log.Log;

import java.util.HashMap;
import java.util.Map;

public class FileCacheManagerV2
{
	private int fileCacheUsageRam = 0;
	private final Map<String, String> fileCacheMap = new HashMap<>();
	private String name;
	private int maxFileSize;
	private int maxFilesCount;
	private int limitRam;

	public FileCacheManagerV2(String name, int maxFileSize, int maxFilesCount, int limitRam)
	{
		this.name = name;
		this.maxFileSize = maxFileSize;
		this.maxFilesCount = maxFilesCount;
		this.limitRam = limitRam;
	}

	public void tryAddFile(String url, String text)
	{
		if (text.length()*4 > maxFileSize)
			Log.warning(name+": Requested file exceeds fileCacheMaxFileSize: "+maxFileSize);
		else if (fileCacheMap.size() > maxFilesCount)
			Log.warning(name+": Requested file exceeds fileCacheMaxFilesCount: "+fileCacheMap.size());
		else if (fileCacheUsageRam + text.length()*4 > limitRam)
			Log.warning(name+": Requested file exceeds fileCacheLimitRam ("+limitRam+"): "+fileCacheUsageRam+" + "+ text.length());
		else
		{
			fileCacheUsageRam += text.length() * 4;
			fileCacheMap.put(url, text);
		}
	}

	public String get(String url)
	{
		return fileCacheMap.getOrDefault(url, null);
	}


	public void clearFileCache()
	{
		fileCacheUsageRam = 0;
		fileCacheMap.clear();
	}

	public int getFileCacheUsageRam()
	{
		return fileCacheUsageRam;
	}

	public Map<String, String> getFileCacheMap()
	{
		return fileCacheMap;
	}


	public boolean exists(String file)
	{
		return fileCacheMap.containsKey(file);
	}
}

