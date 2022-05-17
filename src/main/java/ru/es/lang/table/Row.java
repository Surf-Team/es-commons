package ru.es.lang.table;

import ru.es.log.Log;

import java.util.ArrayList;
import java.util.List;

public class Row
{
    public int id = Integer.MIN_VALUE;
    public List<Entry> entries = new ArrayList<>();

    public void writeValue(String name, String value)
    {
        Entry prop = getEntry(name);
        //Log.warning("write property key: "+name+", old value: "+prop.value+", new value: "+value);
        prop.value = value;
    }


    public Entry getEntry(String name)
    {
        for (Entry p : entries)
        {
            if (p.key.equals(name))
                return p;
        }
        return null;
    }

    public String getValue(String name)
    {
        try
        {
            return getEntry(name).value;
        }
        catch (NullPointerException npe)
        {
            Log.warning("Cant find entry: "+name+" in row "+id+" "+entries.get(0).value);
            throw npe;
        }
    }

    public int getValueInt(String name)
    {
        return Integer.parseInt(getValue(name));
    }

    public int getValueInt(String name, int defaultVal)
    {
        String value = getValue(name);
        if (value == null)
            return defaultVal;
        else
            return Integer.parseInt(value);
    }

    public long getValueLong(String name)
    {
        return Long.parseLong(getValue(name));
    }

    public boolean getValueBoolean(String name)
    {
        String val = getValue(name);
        if (val.equals("1"))
            return true;
        else if (val.equals("0"))
            return false;
        else if (val.equalsIgnoreCase("true"))
            return true;
        else if (val.equalsIgnoreCase("false"))
            return false;

        throw new RuntimeException("Row.Boolean: Wrong value: "+val+" for entry: "+name);
    }


    public boolean getValueBoolean(String name, boolean defaultVal)
    {
        String val = getValue(name);

        if (val == null)
            return defaultVal;

        return getValueBoolean(name);
    }


    public double getValueDouble(String name)
    {
        return Double.parseDouble(getValue(name));
    }
    public float getValueFloat(String name)
    {
        return Float.parseFloat(getValue(name));
    }

    public String getValue(String name, String defaultVal)
    {
        try
        {
            return getEntry(name).value;
        }
        catch (Exception e)
        {
            return defaultVal;
        }
    }


    public Row clone()
    {
        Row ret = new Row();
        ret.id = id;
        for (Entry e : entries)
            ret.entries.add(new Entry(e.key, e.value));

        return ret;
    }

    public void rewrite(Row selectedNpcTemplate)
    {
        entries.clear();
        for (Entry e : selectedNpcTemplate.entries)
        {
            //Log.warning("Rewrite key: "+e.key);
            entries.add(new Entry(e.key, e.value));
        }
    }

    public void add(String key, String value)
    {
        entries.add(new Entry(key, value));
    }

    public void set(String key, String value)
    {
        getEntry(key).value = value;
    }
}
