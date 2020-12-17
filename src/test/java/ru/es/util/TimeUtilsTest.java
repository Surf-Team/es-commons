package ru.es.util;

public class TimeUtilsTest
{
    public static void main(String[] args)
    {
        String timeTo = TimeUtils.convertMillsToFormatedString(11000, true);
        System.out.println(timeTo);
    }
}
