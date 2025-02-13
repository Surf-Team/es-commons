package ru.es.fileCache;

public class FileCacheSettings
{
	public final String name;
	public boolean fileCache = false;
	public int fileCacheMaxFileSize = 100 * 1024 * 1024;
	public int fileCacheMaxFilesCount = 10000;
	public int fileCacheLimitRam = 512 * 1024 * 1024;

	public FileCacheSettings(String name) {this.name = name;}
}
