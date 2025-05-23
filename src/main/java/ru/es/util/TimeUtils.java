package ru.es.util;

import ru.es.log.Log;
import ru.es.math.ESMath;
import ru.es.thread.RunnableImpl;
import ru.es.thread.SingletonThreadPool;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
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

            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(splitTimeOfDay[0]));
            calendar.set(Calendar.MINUTE, Integer.parseInt(splitTimeOfDay[1]));

            if (splitTimeOfDay.length > 2)
            {
                int dayOfWeek = Integer.parseInt(splitTimeOfDay[2]) + 1; // считаем по русски, чтобы первый день был понедельником, а ВС - 7й
                if (dayOfWeek == 8)
                    dayOfWeek = 1;

                calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
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
        List<String> times = new ArrayList<>();
        while (t.hasMoreTokens())
        {
            String s = t.nextToken();
            if (!s.trim().isEmpty())
                times.add(s);
        }

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
        int hours = (int) ((mills / 1000 / 60 / 60) % 24);
        int days = (int) (mills / 1000 / 60 / 60 / 24);

        if (days > 0)
        {
            if (hours == 0)
            {
                if (!ru)
                    return days + " days";
                else
                    return days + " " + daysConverterRu(days);
            }
            else
            {
                if (!ru)
                    return days + " days, " + hours + " hours";
                else
                    return days + " " + daysConverterRu(days) + ", " + hours + " "+hoursConverterRu(hours);
            }
        }
        else if (hours > 0)
        {
            if (minutes == 0)
            {
                if (!ru)
                    return hours + " hours";
                else
                    return hours + " "+hoursConverterRu(hours);
            }
            else
            {
                if (!ru)
                    return hours + " hours, " + minutes + " minutes";
                else
                    return hours + " "+hoursConverterRu(hours)+", " + minutes + " минут";
            }
        }
        else if (minutes > 0)
        {
            if (seconds != 0)
            {
                if (!ru)
                    return minutes + " minutes, " + seconds + " seconds";
                else
                    return minutes + " минут, " + seconds + " секунд";
            }
            else
            {
                if (!ru)
                    return minutes + " minutes";
                else
                    return minutes + " минут";
            }
        }
        else
        {
            if (!ru)
                return seconds + " seconds";
            else
                return seconds + " секунд";
        }
    }
    public static String convertMillsToFormatedString1(long mills, boolean ru)
    {
        long seconds = (mills / 1000) % 60;
        long minutes = (mills / 1000 / 60) % 60;
        int hours = (int) ((mills / 1000 / 60 / 60) % 24);
        int days = (int) (mills / 1000 / 60 / 60 / 24);

        if (days > 0)
        {
            if (!ru)
                return days + " days";
            else
                return days + " " + daysConverterRu(days);
        }
        else if (hours > 0)
        {
            if (!ru)
                return hours + " hours";
            else
                return hours + " "+hoursConverterRu(hours);
        }
        else if (minutes > 0)
        {

            if (!ru)
                return minutes + " minutes";
            else
                return minutes + " минут";
        }
        else
        {
            if (!ru)
                return seconds + " seconds";
            else
                return seconds + " секунд";
        }
    }

    public static String convertMillsToMiniFormat(long mills)
    {
        long seconds = (mills / 1000) % 60;
        long minutes = (mills / 1000 / 60) % 60;
        long hours = (mills / 1000 / 60 / 60) % 24;
        long days = mills / 1000 / 60 / 60 / 24;

        if (days > 0)
        {
            return days + "d";
        }
        else if (hours > 0)
        {
            return hours + "h";
        }
        else if (minutes > 0)
        {
            return minutes + "m";
        }
        else
        {
            return seconds + "s.";
        }
    }

    public static String daysConverterRu(int days)
    {
        days %= 100;


        if (days == 1 || days > 20 && days % 10 == 1)
            return "день";
        else if (days == 2 || days > 20 && days % 10 == 2)
            return "дня";
        else if (days == 3 || days > 20 && days % 10 == 3)
            return "дня";
        else if (days == 4 || days > 20 && days % 10 == 4)
            return "дня";

        return "дней";
    }
    public static String hoursConverterRu(int hours)
    {
        hours %= 100;

        if (hours == 1 || hours > 20 && hours % 10 == 1)
            return "час";
        else if (hours == 2 || hours > 20 && hours % 10 == 2)
            return "часа";
        else if (hours == 3 || hours > 20 && hours % 10 == 3)
            return "часа";
        else if (hours == 4 || hours > 20 && hours % 10 == 4)
            return "часа";

        return "часов";
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
        long minutes = mills / 1000 / 60;

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

    public static SimpleDateFormat currentTimeFormat = new SimpleDateFormat("HH:mm:ss");

    public static String getCurrentTimeString()
    {
        return currentTimeFormat.format(new Date());
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

    public static int getDayOfWeek() // 1 - воскресенье, 2 - понедельник
    {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        return dayOfWeek;
        //Date d = new Date();
        //SimpleDateFormat format1;
        //format1 = new SimpleDateFormat("F");
        //return Integer.parseInt(format1.format(d));
    }

    public static DayOfWeek getDayOfWeekEnum()
    {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        return DayOfWeek.values()[dayOfWeek-1];
    }

    public static void main(String[] args)
    {
        Log.warning("dayOfWeek: "+getDayOfWeek());
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

    public static String getTimeStringFull3(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("yyyy-MM-dd, HH:mm");

        return format1.format(d);
    }

    public static String getTimeStringFull4(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("dd MMM yyyy, HH:mm");

        return format1.format(d);
    }

    public static String getTimeStringFull5(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;

        int targetYear = LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault()).getYear();

        // Получаем текущую дату
        LocalDate currentDate = LocalDate.now();
        // Извлекаем текущий год
        int currentYear = currentDate.getYear();

        if (currentYear != targetYear)
            format1 = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");
        else
            format1 = new SimpleDateFormat("dd MMM, HH:mm:ss");

        return format1.format(d);
    }

    public static String getTimeStringFull6(long time)
    {
        Date d = new Date(time);
        SimpleDateFormat format1;
        format1 = new SimpleDateFormat("dd.MM.yyyy HH:mm");

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

    // example:
    // timeOpen[][][] = {{{14,00}, {15,30}}, {{17,55}, {19,30}}, {{22,50}, {0,20}}};
    public static boolean getBoolean(int[][][] openCloseTime, int hour, int minute)
    {
        //int hour = TimeUtils.getHourOfDay();
        //int minute = TimeUtils.getMinutesOfHour();

        for (int[][] period : openCloseTime)
        {
            int[] openTime = period[0];
            int[] closeTime = period[1];

            int openH = openTime[0];
            int openM = openTime[1];
            int closeH = closeTime[0];
            int closeM = closeTime[1];

            if (openH > closeH) // example: start on 23:00, end on 1:00
            {
                if (hour >= openH)
                {
                    closeH = 24;
                    closeM = 60;
                }
                else if (hour <= closeH)
                {
                    openH = 0;
                    openM = 0;
                }
            }

            if ((hour > openH || hour == openH && minute >= openM) &&
                    (hour < closeH || hour == closeH && minute < closeM))
            {
                return true;
            }
        }
        return false;
    }

    // test
    /*public static void main(String[] args)
    {
        int eachSeconds = 15;
        int minTime = 3;

        long curTimeStamp = System.currentTimeMillis();
        long curTime = System.currentTimeMillis();
        for (int i = 0; i < 20; i++)
        {
            long result = getCondensator(curTime, eachSeconds, minTime);
            long endTime = curTime + result;
            curTime += 1000;
            Log.warning("after "+i+" sec we will waiting for "+result+" ms. End time is: "+(endTime-curTimeStamp));
        }

        System.exit(0);
    } */

    // достать кол-во милисекунд до следующей временной точки, кратной eachSeconds, но не раньше чем через minTime
    public static long getAlignedTime(int eachSeconds, int minTime)
    {
        long curTime = System.currentTimeMillis();
        return getAlignedTime(curTime, eachSeconds, minTime);
    }

    private static long getAlignedTime(long curTime, int eachSeconds, int minTimeSeconds)
    {
        eachSeconds -= minTimeSeconds;

        long eachMillis = eachSeconds * 1000;
        long minLimit = minTimeSeconds * 1000;

        return ESMath.nextAlignedNumber(curTime, eachMillis, minLimit);
    }

    public static long getCurrentSecondInDay()
    {
        LocalDateTime date = LocalDateTime.now();

        return date.toLocalTime().toSecondOfDay();
    }

    // date format: 2023-03-19 18:00:00 +03:00
    public static LocalDateTime getTimeOf(String dateTimeStr)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

    // date format: 2023-03-19 18:00:00 +03:00
    public static ZonedDateTime getTimeOfZoned(String dateTimeStr)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return ZonedDateTime.parse(dateTimeStr, formatter);
    }

    public static String jsFormat(LocalDateTime dateTime, ZoneOffset zoneOffset)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
        return dateTime.atZone(zoneOffset).format(formatter);
    }

    public static String jsFormat(ZonedDateTime dateTime)
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");
        return dateTime.format(formatter);
    }

    // splitter = ;
    public static List<LocalTime> getTimeList(String timeList, String splitter)
    {
        String[] split = timeList.split(splitter);

        List<LocalTime> ret = new ArrayList<>();
        for (String sp : split)
        {
            String[] time = sp.split(":");
            LocalTime localTime = LocalTime.of(Integer.parseInt(time[0]), Integer.parseInt(time[1]));
            ret.add(localTime);
        }

        return ret;
    }

    // преобразуем строку вида 5h 30m 30s в интервал времени в миллисекундах
    public static long parseInterval(String value)
    {
        long ret = 0L;

        String[] tokens = value.split(" ");
        for (String token : tokens)
        {
            if (token.contains("s"))
            {
                token = token.replace("s", "").trim();
                int seconds = Integer.parseInt(token);

                ret+= seconds*1000L;
            }
            else if (token.contains("m"))
            {
                token = token.replace("m", "").trim();
                int minutes = Integer.parseInt(token);

                ret+= minutes*60L*1000L;
            }
            else if (token.contains("h"))
            {
                token = token.replace("h", "").trim();
                int hours = Integer.parseInt(token);

                ret+= hours*60L*60L*1000L;
            }
        }
        return ret;
    }

    // example: 11:00, 12:00, 23:59
    public static String getListOfTime(List<LocalTime> time)
    {
        List<String> times = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        for (LocalTime lt : time)
        {
            times.add(lt.format(formatter));
        }

        return ListUtils.toString(times, ", ");
    }

    public static void runEveryDayAt(int hours, int minutes, Runnable r)
    {
        // Получаем текущее время
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = calendar.get(Calendar.MINUTE);

        // Вычисляем разницу во времени между текущим временем и желаемым временем
        long delay = ((hours - currentHour) * 60 + (minutes - currentMinute)) * 60 * 1000;

        if (delay < System.currentTimeMillis())
            delay += 24*60*60*1000L;

        SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                // Ваш код, который должен быть выполнен в заданное время
                System.out.println("Выполнение задачи в " + hours + ":" + minutes);
                r.run();
            }
        }, delay, 24 * 60 * 60 * 1000);
    }
}
