package ru.es.util.writers;

import java.io.IOException;
import java.net.URL;

public interface UrlByteWriter
{
	void write(URL url, byte[] bytes) throws IOException;
}
