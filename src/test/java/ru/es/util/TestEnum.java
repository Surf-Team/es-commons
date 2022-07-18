package ru.es.util;

import org.junit.jupiter.api.Test;
import ru.es.log.Log;

public class TestEnum
{
	@Test
	public void test()
	{
		Class enumClass = TestEn.class;
		String value = "A1";

		Object valueObj = Enum.valueOf(enumClass, value);

		Log.warning("Value obj: "+valueObj);
	}

	enum TestEn
	{
		A1,
		A2
	}
}
