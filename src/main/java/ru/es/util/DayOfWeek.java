package ru.es.util;

import java.util.Calendar;

public enum DayOfWeek
{
	SUNDAY(Calendar.SUNDAY), // 1
	MONDAY(Calendar.MONDAY), // 2
	TUESDAY(Calendar.TUESDAY), // 3
	WEDNESDAY(Calendar.WEDNESDAY), // 4
	THURSDAY(Calendar.THURSDAY), // 5
	FRIDAY(Calendar.FRIDAY), // 6
	SATURDAY(Calendar.SATURDAY); // 7

	public int index;

	DayOfWeek(int index)
	{
		this.index = index;
	}
}
