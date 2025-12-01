package ru.es.util;

import java.util.Calendar;

public enum DayOfWeek
{
	SUNDAY(Calendar.SUNDAY, new String[] { "Воскресенье", "Sunday" }), // 1
	MONDAY(Calendar.MONDAY, new String[] { "Понедельник", "Monday" }), // 2
	TUESDAY(Calendar.TUESDAY, new String[] { "Вторник", "Tuesday"}), // 3
	WEDNESDAY(Calendar.WEDNESDAY, new String[] { "Среда", "Wednesday"}), // 4
	THURSDAY(Calendar.THURSDAY, new String[] { "Четверг", "Thursday"}), // 5
	FRIDAY(Calendar.FRIDAY, new String[] { "Пятница", "Friday"}), // 6
	SATURDAY(Calendar.SATURDAY, new String[] { "Суббота", "Saturday"}); // 7

	public int index;
	public String[] name;

	DayOfWeek(int index, String[] name)
	{
		this.index = index;
		this.name = name;
	}
}
