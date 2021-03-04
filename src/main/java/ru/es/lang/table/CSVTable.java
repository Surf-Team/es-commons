package ru.es.lang.table;

import ru.es.log.Log;
import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CSVTable extends Table
{
    public CSVTable(File csvFile, String csvId) throws IOException
    {
        readCsv(csvFile, csvId);
    }

    private void readCsv(File csvFile, String csvId) throws IOException
    {
        String[] lines = FileUtils.readLines(csvFile);

        int indexColumnIndex = -1;
        Map<Integer, String> tableHead = new HashMap<>();
        Map<String, Integer> tableHeadRev = new HashMap<>();

        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
            String[] columns = line.split("\t");
            if (i == 0)
            {
                if (csvId != null)
                    indexColumnIndex = CSVUtils.csvFindColumnId(line, csvId);

                for (int c = 0; c < columns.length; c++)
                {
                    tableHead.put(c, columns[c]);
                    tableHeadRev.put(columns[c], c);
                }
            }
            else
            {
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                try
                {
                    int id = i-1;
                    if (csvId != null)
                        id = Integer.parseInt(columns[indexColumnIndex]);

                    Row row = new Row();
                    row.id = id;
                    rows.add(row);
                    for (int c = 0; c < columns.length; c++)
                    {
                        row.entries.add(new Entry(tableHead.get(c), columns[c]));
                    }
                }
                catch (Exception e)
                {
                    Log.warning("Error in line: " + line);
                    e.printStackTrace();
                    throw e;
                }
            }
        }
    }

    @Override
    protected StringBuilder createFileString()
    {
        StringBuilder ret = new StringBuilder();

        Row zeroRow = rows.get(0);
        boolean firstEntry = true;
        for (Entry e : zeroRow.entries)
        {
            if (!firstEntry)
                ret.append("\t");
            else
                firstEntry = false;

            ret.append(e.key);
        }
        ret.append("\r\n");

        for (Row r : rows)
        {
            firstEntry = true;
            for (Entry e : r.entries)
            {
                if (!firstEntry)
                    ret.append("\t");
                else
                    firstEntry = false;
                
                ret.append(e.value);
            }
            ret.append("\r\n");
        }

        return ret;
    }
}
