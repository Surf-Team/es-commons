package ru.es.util;

import org.junit.jupiter.api.Test;
import ru.es.log.Log;

public class HelloTest
{
	@Test
	public void testHello()
	{
		Log.warning("Hello from es-commons! v2");
	}
}
