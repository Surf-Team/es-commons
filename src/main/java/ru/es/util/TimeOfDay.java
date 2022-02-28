package ru.es.util;

public class TimeOfDay
{
	public int hour;
	public int minute;

	public TimeOfDay(int hour, int minute)
	{
		this.hour = hour;
		this.minute = minute;
	}

	public int getSecondInDay()
	{
		int secondIndex = hour * 60*60;
		secondIndex += minute * 60;
		return secondIndex;
	}

}
