package ru.es.util;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by saniller on 07.05.2015.
 */
public class StringUtils
{
    public static int countSymbols(String text, String word)
    {
        int count = 0;
        for (int i = 0; i < text.length(); i++)
        {
            if (text.substring(i, i+1).equals(word))
                count++;
        }
        return count;
    }

    public static int countSymbols(String text, String[] wordArray)
    {
        int count = 0;
        for (int i = 0; i < text.length(); i++)
        {
            for (String word : wordArray)
            {
                if (text.substring(i, i + 1).equals(word))
                {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    public static int getWordIndex(String fullString, String word, String delimiter)
    {
        int index = 0;
        for (String s : ListUtils.getListOfString(fullString, delimiter))
        {
            if (s.equals(word))
            {
                return index;
            }
            index++;
        }
        return -1;
    }

    public static String byteToString(byte[] b, String delim)
    {
        StringBuilder ret = new StringBuilder();
        for (byte bt : b)
        {
            ret.append(bt);
            ret.append(delim);
        }
        return ret.toString();
    }

    public static String byteToString(byte[] b, String delim, int start, int lim)
    {
        StringBuilder ret = new StringBuilder();
        for (int i = start; i < lim; i++)
        {
            ret.append(b[i]);
            ret.append(delim);
        }
        return ret.toString();
    }

    public static String collectionToString(Collection b, String delim)
    {
        StringBuilder ret = new StringBuilder();
        for (Object bt : b)
        {
            ret.append(bt + delim);
        }
        return ret.toString();
    }

    public static<T> String arrayToString(T[] b, String delim)
    {
        StringBuilder ret = new StringBuilder();
        for (T bt : b)
        {
            ret.append(bt + delim);
        }
        return ret.toString();
    }


    public static byte[] stringToByte(String string, String delim)
    {
        StringTokenizer t = new StringTokenizer(string, delim);
        byte[] ret = new byte[t.countTokens()];
        int index = 0;
        while (t.hasMoreTokens())
        {
            String token = t.nextToken();
            ret[index] = Byte.parseByte(token);
            index++;
        }

        return ret;
    }

    public static String stripSlashes(String s)
    {
        if(s == null)
            return "";
        s = s.replace("\\'", "'");
        s = s.replace("\\\\", "\\");
        return s;
    }

    public static String[] toArray(String text, String splitter)
    {
        List<String> list = ListUtils.getListOfString(text, splitter);
        String[] str = new String[list.size()];
        int index = 0;
        for (String s : list)
        {
            str[index] = s;
            index++;
        }
        return str;
    }

    // канал канала каналов, файла файла файлов
    public static String getWordEnding(int num, String string1, String string2to4, String string5to20orZero)
    {
        if (num == 0)
            return string5to20orZero;

        num = Math.abs(num) % 100;

        if (num >= 5 && num <= 20)
            return string5to20orZero;
        else if (num % 10 == 1)
            return string1;
        else if (num % 10 >= 2 && num % 10 <= 4)
            return string2to4;

        return "error";
    }

    public static String numberWithZeros(int number, int size) // 0001 - 4 size
    {
        StringBuilder ret = new StringBuilder();
        ret.append(number);

        while (ret.length() < size)
            ret.insert(0, "0");

        return ret.toString();
    }

    public static String doubleToPercent(double d, int zeros)
    {
        String num = getNumberWithFixedSizeAfterDot(d*100.0, zeros) + "%";
        return num;
    }

    // возвращаем строку из числа double с фиксированным количеством знаков после запятой
    public static String getNumberWithFixedSizeAfterDot(double num, int fixedZeroCount)
    {
        if (Double.isInfinite(num))
            return "Inf";

        float pow = (float) Math.pow(10, fixedZeroCount);

        int a = (int) Math.round(num * pow);

        float b = (float) a / pow;
        return ""+b;
    }

    // возвращаем строку из числа double с фиксированным количеством знаков после запятой
    public static String getNumberWithFixedSizeAfterDot(float num, int fixedZeroCount)
    {
        if (Double.isInfinite(num))
            return "Inf";


        String formattedString = String.format("%."+fixedZeroCount+"f", num);
        formattedString = formattedString.replace(",", ".");
        return formattedString;
    }

    public static String splitDataToLine(String splitter, String... data)
    {
        StringBuilder ret = new StringBuilder();
        int i = 0;
        for (String w : data)
        {
            i++;
            if (i != data.length)
                ret.append(w+splitter);
            else
                ret.append(w);
        }
        return ret.toString();
    }

    public static String splitIntArray(String splitter, int[] array)
    {
        StringBuilder ret = new StringBuilder();
        int i = 0;
        for (int w : array)
        {
            i++;
            if (i != array.length)
                ret.append(w+splitter);
            else
                ret.append(w);
        }
        return ret.toString();
    }

    public static String limit(String text, int letters)
    {
        if (text == null)
            return text;
        
        if (text.length() > letters)
            text = text.substring(0, letters);

        return text;
    }

    public static String getStageName(String[] args)
    {
        String stageName = "developer";
        if (args != null && args.length > 0)
        {
            stageName = "";
            boolean first = true;
            for (String s : args)
            {
                if (!first)
                    stageName += " ";

                stageName += s;

                first = false;
            }
        }
        return stageName;
    }

    // for unix / windows / mac line endings
    public static String[] splitByLines(String text)
    {
        return text.split("\\r?\\n|\\r");
    }


    public static String getStackTrace(Throwable throwable)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);

        throwable.printStackTrace(pw);

        return sw.getBuffer().toString();
    }

    // проверка - не содержит ли строка спец символы
    // true если содержит только буквы/цифры
    // false если есть прочие символы
	public static boolean isAlphaNumeric(String text)
	{
		boolean result = true;
		char[] chars = text.toCharArray();
		for(int i = 0; i < chars.length; i++)
		{
			if(!Character.isLetterOrDigit(chars[i]))
			{
				result = false;
				break;
			}
		}
		return result;
	}
}
