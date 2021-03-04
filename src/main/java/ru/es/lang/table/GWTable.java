package ru.es.lang.table;

import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;

// GodWorld ClientDat Editor parser table
public class GWTable extends Table
{
    public final String idName;

    public GWTable(File f, String idName) throws IOException
    {
        this.idName = idName;
        readClassic(f);
    }

    private void readClassic(File file) throws IOException
    {
        String[] lines = FileUtils.readLines(file);

        for (int i = 0; i < lines.length; i++)
        {
            String[] columns = lines[i].split("\t");

            if (startLine == null)
            {
                startLine = columns[0];
                endLine = columns[columns.length - 1];
            }

            Row row = new Row();
            rows.add(row);

            if (idName == null)
                row.id = i;

            for (int k = 1; k < columns.length - 1; k++)
            {
                String column = columns[k];
                String[] keyValue = column.split("=");

                if (row.id == Integer.MIN_VALUE)
                {
                    if (keyValue[0].equals(idName))
                        row.id = Integer.parseInt(keyValue[1]);
                }

                Entry properties = new Entry(keyValue[0], keyValue[1]);
                row.entries.add(properties);
            }

            if (row.id == Integer.MIN_VALUE)
                throw new RuntimeException("Cant find id in file: " + file.getName() + ", line: " + i + ", idName: " + idName);
        }
    }

    @Override
    protected StringBuilder createFileString()
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Row p : rows)
        {
            if (!first)
                sb.append("\r\n");

            first = false;

            sb.append(startLine);
            sb.append("\t");

            for (Entry pp : p.entries)
            {
                sb.append(pp.key);
                sb.append("=");
                sb.append(pp.value);
                sb.append("\t");
            }
            sb.append(endLine);
        }
        return sb;
    }

    public static void readFromClassic(Row to, String line, String idName)
    {
        String[] columns = line.split("\t");

        for (int k = 1; k < columns.length - 1; k++)
        {
            String column = columns[k];
            String[] keyValue = column.split("=");

            if (idName != null && to.id == Integer.MIN_VALUE)
            {
                if (keyValue[0].equals(idName))
                    to.id = Integer.parseInt(keyValue[1]);
            }

            to.entries.add(new Entry(keyValue[0], keyValue[1]));
        }

        if (to.id == Integer.MIN_VALUE && idName != null)
            throw new RuntimeException("Cant find ID for row line, line: " + line + ", idName: " + idName);
    }
}
