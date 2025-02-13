package ru.es.util;

/**
 * Created by saniller on 04.06.2016.
 */
public class BitUtils
{
    public static String getBitsInt(int num)
    {
        String ret = "";

        for (byte i = 31; i >= 0; i--)
        {
            if (isSetInt(num, i))
                ret += "1";
            else
                ret += "0";
        }

        return ret;
    }

    public static String getBitsShort(short num)
    {
        String ret = "";

        for (byte i = 15; i >= 0; i--)
        {
            if (isSetShort(num, i))
                ret += "1";
            else
                ret += "0";
        }

        return ret;
    }

    public static String getBitsByte(byte num)
    {
        String ret = "";

        for (byte i = 7; i >= 0; i--)
        {
            if (isSetByte(num, i))
                ret += "1";
            else
                ret += "0";
        }

        return ret;
    }

    // num 0x00000000 - 0xFFFFFFFF
    // bit index (0 - 31)
    public static boolean isSetInt(int num, byte bitIndex)
    {
        if ((num | (1 << bitIndex)) == num)
            return true;
        else
            return false;
    }

    public static boolean isSetShort(short num, byte bitIndex)
    {
        if ((num | (1 << bitIndex)) == num)
            return true;
        else
            return false;
    }

    public static boolean isSetByte(byte num, byte bitIndex)
    {
        if ((num | (1 << bitIndex)) == num)
            return true;
        else
            return false;
    }

    public static int setBitInt(int value, byte bitIndex, boolean set)
    {
        if (set)
            value |= 1 << bitIndex;
        else
            value &= ~(1 << bitIndex);

        return value;
    }

    public static short setBitShort(short value, byte bitIndex, boolean set)
    {
        if (set)
            value |= 1 << bitIndex;
        else
            value &= ~(1 << bitIndex);

        return value;
    }

    public static byte setBitByte(byte value, byte bitIndex, boolean set)
    {
        if (set)
            value |= 1 << bitIndex;
        else
            value &= ~(1 << bitIndex);

        return value;
    }
}
