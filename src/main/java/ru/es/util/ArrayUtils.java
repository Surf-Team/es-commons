package ru.es.util;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * Created by saniller on 16.08.2015.
 */
public class ArrayUtils
{
    public static <T> boolean contains(T[] array, T value) {
        if(array == null) {
            return false;
        } else {
            for(int i = 0; i < array.length; ++i) {
                if(value == array[i]) {
                    return true;
                }
            }

            return false;
        }
    }

    public static <T> boolean containsEqual(T[] array, T value) {
        if(array == null) {
            return false;
        } else {
            for(int i = 0; i < array.length; ++i) {
                if(value.equals(array[i])) {
                    return true;
                }
            }

            return false;
        }
    }

    public static boolean contains(int[] array, int value) {
        if(array == null) {
            return false;
        } else {
            for(int i = 0; i < array.length; ++i) {
                if(value == array[i]) {
                    return true;
                }
            }

            return false;
        }
    }

    public static<T> List<T> toList(T[] array)
    {
        List<T> t = new ArrayList<>();
        for (T ta : array)
        {
            t.add(ta);
        }
        return t;
    }
    
    public static List<Integer> toIntList(int[] array)
    {
        List<Integer> t = new ArrayList<>();
        for (int ta : array)
        {
            t.add(ta);
        }
        return t;
    }

    public static int indexOf(byte[] sequence, byte[] fullText)
    {
        int index = 0;
        for (byte b : fullText)
        {
            if (b == sequence[0])
            {
                boolean found = false;
                for (int i = 1; i < sequence.length; i++)
                {
                    if (sequence[i] != fullText[index+i])
                    {
                        found = false;
                        break;
                    }
                    else
                        found = true;
                }
                if (found)
                    return index;

                index++;
            }
            index++;
        }
        return -1;
    }

    public static<T> int indexOf(T[] arr, T element)
    {
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == element)
                return i;
        }
        return -1;
    }

    public static byte[] getBytes(byte[] array, int from, int to)
    {
        byte[] ret = new byte[to-from];
        int index = 0;
        for (int i = from; i < to; i++)
        {
            ret[index] = array[i];
            index++;
        }
        return ret;
    }

    public static String byteToString(byte[] array)
    {
        try
        {
            return new String(array, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static String byteToString(byte[] array, int maxSize, boolean maxSizeFromBack)
    {
        try
        {
            String ret = new String(array, "UTF-8");
            if (ret.length() > maxSize)
            {
                if (!maxSizeFromBack)
                    return ret.substring(0, maxSize);
                else
                    return ret.substring(ret.length()-maxSize);
            }
            else
                return ret;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] floatArray2ByteArray(float[] values)
    {
        ByteBuffer buffer = ByteBuffer.allocate(4 * values.length);

        for (float value : values){
            buffer.putFloat(value);
        }

        return buffer.array();
    }

    public static long getCheckSum(byte[] bytes)
    {
        Checksum checksum = new CRC32();

        // update the current checksum with the specified array of bytes
        checksum.update(bytes, 0, bytes.length);

        // get the current checksum value
        return checksum.getValue();
    }

    public static String intToString(int[] notes, String delim)
    {
        StringBuilder ret = new StringBuilder();
        for (int val : notes)
        {
            ret.append(val);
            ret.append(delim);
        }
        return ret.toString();
    }

    public static int[] stringToIntArray(String input, String delim)
    {
        String[] spl = input.split(delim);
        int[] ret = new int[spl.length];
        int i = 0;
        for (String s : spl)
        {
            ret[i] = Integer.parseInt(s);
            i++;
        }
        return ret;
    }
}
