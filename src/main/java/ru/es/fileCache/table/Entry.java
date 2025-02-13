package ru.es.fileCache.table;

import java.io.Serializable;

public class Entry implements Serializable
{
    public String key;
    public String value;

    public Entry(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public Entry(String key, int value)
    {
        this.key = key;
        this.value = String.valueOf(value);
    }
}
