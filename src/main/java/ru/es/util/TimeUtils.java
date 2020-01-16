package ru.es.util;

import javolution.util.FastTable;
import ru.es.log.Log;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 11.12.14
 * Time: 14:53
 * To change this template use File | Settings | File Templates.
 */
public class TimeUtils
{
    public static long getSmallestMillsFromPeriods(String[] times)
    {
        Calendar currentTime = Calendar.getInstance();
        Calendar ret = null;
        Calendar calendar = null;

        for(String timeOfDay : times)
        {
            String[] splitTimeOfDay = timeOfDay.split(":");
            calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));

            if (splitTimeOfDay.length > 2)
            {
                calendar.set(Calendar.DAY_OF_WEEK, Integer.parseInt(splitTimeOfDay[2]));
                if(calendar.getTimeInMillis() < currentTime.getTimeInMillis())
                {
                    calendar.add(Calendar.DAY_OF_WEEK, 7);
                }
            }
            else
            {
                if(calendar.getTimeInMillis() < currentTime.getTimeInMillis())
                {
                    calendar.add(Calendar.DAY_OF_WEEK, 1);
                }
            }

            // Check for the test date to be the minimum (smallest in the specified list)
            if(ret == null || calendar.getTimeInMillis() < ret.getTimeInMillis())
            {
                ret = calendar;
            }
        }
        if (ret != null)
        {
            return ret.getTimeInMillis() - System.currentTimeMillis();
        }
        else
        {
            Log.warning("error in schedudle start of Event");
            return -1;
        }
    }


    // 21:00:1,21:00:4,16:00 - это значит что будет проводиться в воскресенье и в среду, и ещё каждый день в 16:00
    public static long getSmallestMillsFromPeriods(String fromString) throws Exception
    {
        StringTokenizer t = new StringTokenizer(fromString, ",");
        List<String> times = new FastTable<String>();
        while (t.hasMoreTokens())
        {
            String s = t.nextToken();
            if (!s.trim().isEmpty())
                times.add(s);
        }
        Log.warning("times size: "+times.size());

        if (times.size() == 0)
            throw new Exception("getSmallestMillsFromPeriods: Wrong input string for time. String: "+fromString);
        else
        {
            long ret = getSmallestMillsFromPeriods(times.toArray(new String[times.size()]));
            if (ret == -1)
                throw new Exception("getSmallestMillsFromPeriods: Wrong input string for time (2). String: "+fromString);
            else
                return ret;
        }
    }

    public static String convertMillsToFormatedString(long mills, boolean ru)
    {
        long seconds = (mills / 1000) % 60;
        long minutes = (mills / 1000 / 60) % 60;
        long hours = (mills / 1000 / 60 / 60) % 24;
        long days = mills / 1000 / 60 / 60 / 24;
        if (!ru)
            return ""+days+" days, "+hours+" hours, "+minutes+" minutes, "+seconds+" seconds.";
        else
            return ""+days+" дней, "+hours+" часов, "+minutes+" минут, "+seconds+" секунд.";
    }

    public static String getSecsMillis(double seconds, int maxNumsAfterDotForMs)
    {
        String secondsString = "";
        String msString = "";
        if (seconds >= 1)
            secondsString = (int) seconds + " s";

        double ms = (seconds * 1000) % 1000;
        if (ms >= 1 || maxNumsAfterDotForMs == 0)
            msString = (int) ms+" ms";
        else if (ms > 0)
            msString = StringUtils.getNumberWithFixedSizeAfterDot(ms, maxNumsAfterDotForMs) + " ms";

        if (!msString.isEmpty() && !secondsString.isEmpty())
            return secondsString + ", "+msString;
        else if (secondsString.isEmpty())
            return msString;
        else// if (msString.isEmpty())
            return secondsString;
    }

    public static String convertMillsToFormatedStringMinSec(long mills, boolean ru)
    {
        long seconds = (mills / 1000) % 60;
        long minutes = (mills / 1000 / 60) % 60;

        if (!ru)
            return ""+minutes+" minutes, "+seconds+" seconds";
        else
        {
            if (seconds == 0)
            {
                return convertMillsToFormatedStringMin((int) minutes, ru);
            }
            if (minutes == 0)
            {
                return seconds+" секунд";
            }

            if (minutes != 13 && minutes % 10 == 3 || minutes != 12 && minutes % 10 == 2)
                return "" + minutes + " минуты, " + seconds + " секунд";
            else if (minutes != 11 && minutes % 10 == 1)
                return "" + minutes + " минута, " + seconds + " секунд";
            else
                return "" + minutes + " минут, " + seconds + " секунд";
        }
    }

    public static String convertMillsToFormatedStringMin(int minutes, boolean ru)
    {
        if (!ru)
            if (minutes == 1)
                return ""+minutes+" minute";
            else
                return ""+minutes+" minutes";
        else
        {
            if (minutes != 13 && minutes % 10 == 3 || minutes != 12 && minutes % 10 == 2)
                return "" + minutes + " минуты";
            else if (minutes != 11 && minutes % 10 == 1)
                return "" + minutes + " минута";
            else
                return "" + minutes + " минут";
        }
    }

    public static String getCurrentTimeString()
    {
        Date d = new Date();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("HH:mm:ss");

        return format1.format(d);
    }

    public static String getCurrentYear()
    {
        Date d = new Date();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yyyy");

        return format1.format(d);
    }

    public static String getCurrentTimeStringFull()
    {
        Date d = new Date();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yyyy MMM dd, HH:mm:ss");

        return format1.format(d);
    }

    public static String getCurrentTimeStringForFile()
    {
        Date d = new Date();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yy-MM-dd HH-mm-ss");

        return format1.format(d);
    }

    public static String getCurrentTimeStringForFileBig()
    {
        Date d = new Date();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yy MMM dd HH-mm-ss");

        return format1.format(d);
    }

    public static int getDayOfWeek() // считаем с нуля. 0 - понедельник
    {
        Date d = new Date();
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("F");

        return Integer.parseInt(format1.format(d));
    }

    public static String getTimeString(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("HH:mm:ss");

        return format1.format(d);
    }

    public static String getDateString(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("dd-MM-yy");

        return format1.format(d);
    }

    public static String getTimeForLog()
    {
        Date d = new Date(System.currentTimeMillis());
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("[HH:mm:ss]: ");

        return format1.format(d);
    }

    public static String getMiniTimeString(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("mm:ss");

        return format1.format(d);
    }

    public static String getMinutesSecondsMillis(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("mm:ss:SSS");

        return format1.format(d);
    }

    public static String getTimeStringSecondMilisecond(long time)
    {
        int millis = (int) (time % 1000);
        long seconds = time / 1000;

        return seconds + "."+StringUtils.numberWithZeros(millis, 3);
    }

    public static String getTimeStringFull(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yyyy MMM dd, HH:mm:ss");

        return format1.format(d);
    }

    public static String getTimeStringFull2(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("MMM dd, HH:mm:ss");

        return format1.format(d);
    }

    public static String getTimeStringFullForFile(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yyyy MMM dd, HH-mm-ss");

        return format1.format(d);
    }

    public static String parseLongToString(long millis)
    {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(millis);
        int mounth = time.get(Calendar.MONTH);
        int day = time.get(Calendar.DAY_OF_MONTH);
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minutes = time.get(Calendar.MINUTE);

        String minuteZero ="";
        String mounthZero = "";
        if (minutes < 10)
            minuteZero = "0"+minutes;
        else
            minuteZero = ""+minutes;

        if (mounth < 10)
            mounthZero = "0"+(mounth+1);
        else
            mounthZero = ""+(mounth+1);

        return day+"."+mounthZero+" "+hour+":"+minuteZero;
    }

    public static Date getCurrentDayZeroTime()
    {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.set(Calendar.MILLISECOND, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.HOUR_OF_DAY, 0);
        return time.getTime();
    }

    public static long getDayZeroTime(long millis)
    {
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(millis);
        time.set(Calendar.MILLISECOND, 0);
        time.set(Calendar.SECOND, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.HOUR_OF_DAY, 0);
        return time.getTime().getTime();
    }

    public static int getHourOfDay()
    {
        GregorianCalendar newCal = new GregorianCalendar();
        int dow = newCal.get(Calendar.HOUR_OF_DAY);

        return dow;
    }

    public static int getMinutesOfHour()
    {
        GregorianCalendar newCal = new GregorianCalendar();
        int dow = newCal.get(Calendar.MINUTE);

        return dow;
    }
}
