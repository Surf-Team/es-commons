package ru.es.util;

import java.time.ZonedDateTime;
import java.util.Properties;

public class ESProperties extends Properties
{
	public boolean getProperty(String name, boolean defaultVal)
	{
		String value = getProperty(name, ""+defaultVal);
		return Boolean.parseBoolean(value);
	}

	public int getProperty(String name, int defaultVal)
	{
		String value = getProperty(name, ""+defaultVal);
		return Integer.parseInt(value);
	}

	public long getProperty(String name, long defaultVal)
	{
		String value = getProperty(name, ""+defaultVal);
		return Long.parseLong(value);
	}

	public ZonedDateTime getDateTime(String name) // format: 2023-03-19 18:00:00 +03:00
	{
		String value = getProperty(name);
		return TimeUtils.getTimeOfZoned(value);
	}

	public double getProperty(String name, double defaultVal)
	{
		String value = getProperty(name, ""+defaultVal);
		return Double.parseDouble(value);
	}



}
