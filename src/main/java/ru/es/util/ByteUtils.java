package ru.es.util;

public class ByteUtils
{
    public static boolean contains(byte[] data, byte[] what, boolean fromEnd)
    {
        return indexOf(data, what, fromEnd, data.length) != -1;
    }

    public static boolean contains(byte[] data, byte[] what, boolean fromEnd, int limit)
    {
        return indexOf(data, what, fromEnd, limit) != -1;
    }


    public static int indexOf(byte[] data, byte[] what, boolean fromEnd)
    {
        return indexOf(data, what, false, data.length);
    }


    public static int indexOf(byte[] data, byte[] what, boolean fromEnd, int limit)
    {
        int symbolLen = what.length;

        int endSearch = limit - symbolLen;
        if (endSearch < 0)
            return -1;

        if (symbolLen == 1)
        {
            if (fromEnd)
            {
                for (int i = endSearch; i >= 0; i--)
                {
                    if (data[i] == what[0])
                        return i;
                }
            }
            else
            {
                for (int i = 0; i <= endSearch; i++)
                {
                    if (data[i] == what[0])
                        return i;
                }
            }

            return -1;
        }
        else if (symbolLen == 2)
        {
            if (fromEnd)
            {
                for (int i = endSearch; i >= 0; i--)
                {
                    if (data[i] == what[0] && data[i]+1 == what[1])
                        return i;
                }
            }
            else
            {
                for (int i = 0; i <= endSearch; i++)
                {
                    if (data[i] == what[0] && data[i]+1 == what[1])
                        return i;
                }
            }

            return -1;
        }
        else if (symbolLen == 4)
        {
            if (fromEnd)
            {
                for (int i = endSearch; i >= 0; i--)
                {
                    if (data[i] == what[0] &&
                            data[i]+1 == what[1] &&
                            data[i]+2 == what[2] &&
                            data[i]+3 == what[3])
                        return i;
                }
            }
            else
            {
                for (int i = 0; i <= endSearch; i++)
                {
                    if (data[i] == what[0] &&
                            data[i]+1 == what[1] &&
                            data[i]+2 == what[2] &&
                            data[i]+3 == what[3])
                        return i;
                }
            }

            return -1;
        }
        else
        {
            boolean contains = false;
            if (fromEnd)
            {
                for (int i = endSearch; i >= 0; i--)
                {
                    contains = false;
                    for (int k = 0; k < symbolLen; k++)
                    {
                        if (data[i+k] != what[i+k])
                            break;
                        else
                            contains = true;
                    }
                    if (contains)
                        return i;
                }
            }
            else
            {
                for (int i = 0; i <= endSearch; i++)
                {
                    contains = false;
                    for (int k = 0; k < symbolLen; k++)
                    {
                        if (data[i+k] != what[i+k])
                            break;
                        else
                            contains = true;
                    }
                    if (contains)
                        return i;
                }
            }

            return -1;
        }
    }


    public static int indexOf(byte[] data, byte[] what, int fromIndex, int limit)
    {
        int symbolLen = what.length;

        int endSearch = limit - symbolLen;
        if (endSearch < 0)
            return -1;

        if (symbolLen == 1)
        {
            for (int i = fromIndex; i <= endSearch; i++)
            {
                if (data[i] == what[0])
                    return i;
            }

            return -1;
        }
        else if (symbolLen == 2)
        {
            for (int i = fromIndex; i <= endSearch; i++)
            {
                if (data[i] == what[0] && data[i]+1 == what[1])
                    return i;
            }

            return -1;
        }
        else if (symbolLen == 4)
        {
            for (int i = fromIndex; i <= endSearch; i++)
            {
                if (data[i] == what[0] &&
                        data[i]+1 == what[1] &&
                        data[i]+2 == what[2] &&
                        data[i]+3 == what[3])
                    return i;
            }

            return -1;
        }
        else
        {
            boolean contains = false;
            for (int i = fromIndex; i <= endSearch; i++)
            {
                contains = false;
                for (int k = 0; k < symbolLen; k++)
                {
                    if (data[i+k] != what[i+k])
                        break;
                    else
                        contains = true;
                }
                if (contains)
                    return i;
            }

            return -1;
        }
    }


    public static int toInt(byte[] array, int from, int len, boolean up)
    {
        byte[] ret = new byte[len];

        int value = 0;
        if (up)
        {
            for (int i = from; i < from+len; i++)
            {
                value = (value << 8) + (array[i] & 0xFF);
            }
        }
        else
        {
            for (int i = from+len-1; i >= from; i--)
            {
                value = (value << 8) + (array[i] & 0xFF);
            }
        }
        return value;
    }

    public static float toFloat(byte[] array, int from, int len, boolean up)
    {
        byte[] ret = new byte[len];

        int value = 0;
        if (up)
        {
            for (int i = from; i < from+len; i++)
            {
                value = (value << 8) + (array[i] & 0xFF);
            }
        }
        else
        {
            for (int i = from+len-1; i >= from; i--)
            {
                value = (value << 8) + (array[i] & 0xFF);
            }
        }

        return Float.intBitsToFloat(value);
    }

}
