package ru.es.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestWriteToUrl
{
	@Test
	public void testWriteToURL() throws IOException
	{
		// оба варианта возможны и работают
		//URL url = new URL("file:./someFile.txt");
		//URL url = new URL("http://188.246.224.111/Gve-Server-Data/config/someFile.txt");
		//byte[] content = "content".getBytes(StandardCharsets.UTF_8);
		//FileUtils.writeToURL(url, content);
	}
}
