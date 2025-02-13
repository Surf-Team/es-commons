package ru.es.util;

import org.junit.jupiter.api.Test;
import ru.es.log.Log;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

public class TestTime
{
	@Test
	public void testTime()
	{
		var now = LocalDateTime.now();
		var str = TimeUtils.jsFormat(now, ZoneOffset.ofHours(+3));
		Log.warning("Test JS Time: "+str);
	}
}
