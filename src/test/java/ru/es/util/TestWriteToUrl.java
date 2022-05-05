package ru.es.util;

import org.junit.jupiter.api.Test;
import ru.es.lang.table.Row;
import ru.es.lang.table.TSVTable;

import java.io.IOException;
import java.net.URL;

public class TestWriteToUrl
{
	@Test
	public void testWriteFileToURL() throws IOException
	{
		// оба варианта возможны и работают
		//URL url = new URL("file:./someFile.txt");
		//URL url = new URL("http://188.246.224.111/Gve-Server-Data/config/someFile.txt");
		//byte[] content = "content".getBytes(StandardCharsets.UTF_8);
		//FileUtils.writeToURL(url, content);
	}

	@Test
	public void testWriteTSVTableToURL() throws IOException
	{
		// оба варианта возможны и работают
		//URL url = new URL("file:./someFile.txt");
		/*URL url = new URL("http://188.246.224.111/Gve-Server-Data/config/someFile.txt");
		TSVTable table = new TSVTable();
		table.file = url;
		Row row = table.addRow();
		row.add("some key", "some value");

		table.write();*/
		//FileUtils.writeToURL(url, content);
	}
}
