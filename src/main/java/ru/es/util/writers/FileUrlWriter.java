package ru.es.util.writers;

import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class FileUrlWriter implements UrlByteWriter
{
	@Override
	public void write(URL url, byte[] bytes) throws IOException
	{
		FileUtils.writeFile(new File(url.getFile()), bytes);
	}
}
