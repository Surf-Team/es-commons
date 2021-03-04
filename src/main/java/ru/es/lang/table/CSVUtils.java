package ru.es.lang.table;

import ru.es.log.Log;
import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CSVUtils
{
    public static Map<Integer, String> getMapFromCSV(File csvFile, String csvId, String csvColumn) throws IOException
    {
        String[] lines = FileUtils.readLines(csvFile);

        int dataColumnIndex = -1;
        int indexColumnIndex = -1;

        Map<Integer, String> ret = new HashMap<>();
        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];
            if (i == 0)
            {
                dataColumnIndex = csvFindColumnId(line, csvColumn);
                indexColumnIndex = csvFindColumnId(line, csvId);
            }
            else
            {
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] columns = line.split("\t");

                try
                {
                    int id = Integer.parseInt(columns[indexColumnIndex]);
                    ret.put(id, columns[dataColumnIndex]);
                }
                catch (Exception e)
                {
                    Log.warning("Error in line: "+line);
                    e.printStackTrace();
                    throw e;
                }
            }
        }

        return ret;
    }

    public static int csvFindColumnId(String firstLine, String column)
    {
        int id = 0;
        for (String row : firstLine.split("\t"))
        {
            if (row.equals(column))
                return id;
            id++;
        }
        throw new RuntimeException("Can't find column "+column);
    }
}
