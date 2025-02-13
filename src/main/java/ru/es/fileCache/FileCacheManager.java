package ru.es.fileCache;

import ru.es.log.Log;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class FileCacheManager
{
    private int fileCacheUsageRam = 0;
    private final Map<URL, byte[]> fileCacheMap = new HashMap<>();
    private String name;
    private int maxFileSize;
    private int maxFilesCount;
    private int limitRam;

    public FileCacheManager(String name, int maxFileSize, int maxFilesCount, int limitRam)
    {
        this.name = name;
        this.maxFileSize = maxFileSize;
        this.maxFilesCount = maxFilesCount;
        this.limitRam = limitRam;
    }

    public void tryAddFile(URL f, byte[] bytes)
    {
        if (bytes.length > maxFileSize)
            Log.warning(name+": Requested file exceeds fileCacheMaxFileSize: "+bytes.length);
        else if (fileCacheMap.size() > maxFilesCount)
            Log.warning(name+": Requested file exceeds fileCacheMaxFilesCount: "+fileCacheMap.size());
        else if (fileCacheUsageRam + bytes.length > limitRam)
            Log.warning(name+": Requested file exceeds fileCacheLimitRam ("+limitRam+"): "+fileCacheUsageRam+" + "+ bytes.length);
        else
        {
            fileCacheUsageRam += bytes.length;
            fileCacheMap.put(f, bytes);
        }
    }

    public byte[] get(URL f)
    {
        return fileCacheMap.getOrDefault(f, null);
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

    public Map<URL, byte[]> getFileCacheMap()
    {
        return fileCacheMap;
    }


    public boolean exists(URL file)
    {
        return fileCacheMap.containsKey(file);
    }
}
