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
        return getEntry(name).value;
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
}
