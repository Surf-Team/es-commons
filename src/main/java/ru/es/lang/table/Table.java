package ru.es.lang.table;

import ru.es.lang.StringCall;
import ru.es.log.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Table
{
    public List<Row> rows = new ArrayList<>(1000);
    protected String startLine = null;
    protected String endLine;

    public Table()
    {

    }


    public void setValueByMap(Map<Integer, String> valuesMap, String key)
    {
        for (Row row : rows)
        {
            for (Entry pp : row.entries)
            {
                if (pp.key.equals(key))
                {
                    if (valuesMap.containsKey(row.id))
                    {
                        pp.value = valuesMap.get(row.id);
                    }
                }
            }
        }
    }

    public Map<Integer, String> getHrefMap(String classicReplaceName, Map<Integer, String> replaceMap)
    {
        Map<Integer, String> ret = new HashMap<>();
        for (Row row : rows)
        {
            for (Entry pp : row.entries)
            {
                if (pp.key.equals(classicReplaceName))
                {
                    if (replaceMap.containsKey(row.id))
                    {
                        ret.put(Integer.parseInt(pp.value), replaceMap.get(row.id));
                    }
                }
            }
        }

        return ret;
    }


    public void writeFile(File f) throws IOException
    {
        StringBuilder sb = createFileString();
        FileOutputStream fileOutputStream = new FileOutputStream(f);
        fileOutputStream.write(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    protected abstract StringBuilder createFileString();

    public void replaceValue(String rowName, StringCall replacer)
    {
        boolean hasRowName = false;
        for (Row row : rows)
        {
            hasRowName = false;
            for (Entry cp : row.entries)
            {
                if (cp.key.equals(rowName))
                {
                    cp.value = replacer.call(cp.value);
                    hasRowName = true;
                }
            }
            if (!hasRowName)
            {
                Log.warning("Cant find row name: "+rowName+" on replace value.");
            }
        }
    }

    // индекс идёт по счёту. Замена идёт по единственной property
    public void replaceByIndex(Map<Integer, String> nameHrefMap)
    {
        int id = 0;
        for (Row p : rows)
        {
            if (nameHrefMap.containsKey(id))
                p.entries.get(0).value = nameHrefMap.get(id);
            id++;
        }
    }

    public void checkDuplicatedValues(String column, DuplicateReplacer replacer)
    {
        Map<String, Integer> values = new HashMap<>();
        int currentLineIndex = 0;
        for (Row baseRow : rows)
        {
            for (Entry cp : baseRow.entries)
            {
                if (cp.key.equals(column))
                {
                    // тут проверка на Lower case не используется, т.к. в классике разные регистры = разные названия
                    if (values.containsKey(cp.value))
                    {
                        Log.warning("Duplicated value: " + cp.value);
                        int existIndex = values.get(cp.value);

                        cp.value = replacer.rename(cp.value, baseRow, currentLineIndex, existIndex);

                        Log.warning("Renamed to: " + cp.value);

                        if (values.containsKey(cp.value))
                            Log.warning("Duplicated value after replacer: " + cp.value);
                    }
                    else
                        values.put(cp.value, currentLineIndex);
                }
            }
            currentLineIndex++;
        }
    }

    public Map<Integer, String> findAbsent(Map<Integer, String> descriptions)
    {
        Map<Integer, String> desc = new HashMap<>();
        desc.putAll(descriptions);
        for (Row p : rows)
        {
            desc.remove(p.id);
        }
        return desc;
    }

    public Row getRowWithId(int index)
    {
        for (Row p : rows)
        {
            if (p.id == index)
                return p;
        }
        return null;
    }

    public Row getRowByValue(String key, String value)
    {
        for (Row row : rows)
        {
            for (Entry p : row.entries)
            {
                if (p.key.equals(key) && p.value.equals(value))
                    return row;
            }
        }
        return null;
    }

    public List<Row> getRowsByValue(String key, String value)
    {
        List<Row> ret = new ArrayList<>();
        for (Row row : rows)
        {
            for (Entry p : row.entries)
            {
                if (p.key.equals(key) && p.value.equals(value))
                    ret.add(row);
            }
        }
        return ret;
    }


    public Row addRow()
    {
        Row gameClientNameRow = new Row();
        rows.add(gameClientNameRow);
        return gameClientNameRow;
    }

    public Map<Integer, String> createValuesMap(String column)
    {
        Map<Integer, String> ret = new HashMap<>();

        for (Row r : rows)
        {
            ret.put(r.id, r.getEntry(column).value);
        }

        return ret;
    }
}
