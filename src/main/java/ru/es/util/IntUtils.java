package ru.es.util;

import java.util.IllegalFormatException;

/**
 * Created by saniller on 07.05.2015.
 */
public class IntUtils
{
    public static int parseIntFromString(String integer16or10) throws IllegalFormatException
    {
        int integer = 0;
        if (integer16or10.startsWith("0x"))
        {
            integer16or10 = integer16or10.substring(2);
            integer = Integer.parseInt(integer16or10, 16);
        }
        else
            integer = Integer.parseInt(integer16or10, 10);

        return integer;
    }
}
