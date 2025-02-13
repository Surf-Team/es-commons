package ru.es.lang;

import ru.es.util.TimeUtils;

public class ClockInterval
{
	public int fromHours;
	public int fromMinutes;
	public int toHours;
	public int toMinutes;

	@Override
	public String toString()
	{
		return "{" +
				"fromHours=" + fromHours +
				", fromMinutes=" + fromMinutes +
				", toHours=" + toHours +
				", toMinutes=" + toMinutes +
				'}';
	}

	public boolean isNow()
	{
		int currentMinutes = TimeUtils.getMinutesOfHour();
		int currentHour = TimeUtils.getHourOfDay();

		if (fromHours < currentHour || fromHours <= currentHour && fromMinutes <= currentMinutes)
		{
			if (currentHour < toHours || currentHour <= toHours && currentMinutes <= toMinutes)
			{
				return true;
			}
		}
		return false;
	}
}
