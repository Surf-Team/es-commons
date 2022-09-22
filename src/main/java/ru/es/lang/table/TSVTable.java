package ru.es.lang.table;

import ru.es.lang.Filter;
import ru.es.log.Log;
import ru.es.util.FileUtils;
import ru.es.util.ListUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TSVTable extends Table
{
    // может быть null, если таблица не читалась из файла, а просто была создана
    public URL file;
    public Map<TSVTable, Filter<Row>> subTables = new HashMap<>();

    public TSVTable()
    {

    }

    public TSVTable(File csvFile, String csvId) throws IOException
    {
        this.file = csvFile.toURI().toURL();
        readCsv(file, csvId);
    }

    public TSVTable(URL csvFile, String csvId) throws IOException
    {
        this.file = csvFile;
        readCsv(file, csvId);
    }

    public TSVTable(String text, String id)
    {
        String[] lines = text.split("\n");
        createTableFromStringArray(lines, id);
    }

    // create table with only one column (for later usage)
    public TSVTable(TSVTable from, String idColumnName)
    {
        for (int i = 0; i < from.rows.size(); i++)
        {
            Row r = from.rows.get(i);
            int id = r.id;

            Row newRow = new Row();
            newRow.add(idColumnName, ""+id);

            rows.add(newRow);
        }
    }

    private void readCsv(URL csvFile, String csvId) throws IOException
    {
        String[] lines = FileUtils.readLines(csvFile);
        createTableFromStringArray(lines, csvId);
    }

    private void createTableFromStringArray(String[] lines, String csvId)
    {
        int indexColumnIndex = -1;
        Map<Integer, String> tableHead = new HashMap<>();
        Map<String, Integer> tableHeadRev = new HashMap<>();

        for (int i = 0; i < lines.length; i++)
        {
            String line = lines[i];

            // -1 позволяет учитывать пустые значения, а иначе два \t\t разбиваются в 1
            String[] columns = line.split("\t", -1);

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
    public StringBuilder createFileString()
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

    public void pasteColumn(String oldName, String newName, TSVTable from)
    {
        for (int i = 0; i < rows.size(); i++)
        {
            rows.get(i).add(newName, from.rows.get(i).getValue(oldName));
        }
    }

    public void pasteColumn(String[] oldNames, String newName, TSVTable from)
    {
        List<String> tmpString = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++)
        {
            Row fromRow = from.rows.get(i);
            for (String s : oldNames)
            {
                String tmp = fromRow.getValue(s);
                if (!tmp.isEmpty())
                    tmpString.add(tmp);
            }
            rows.get(i).add(newName, ListUtils.toString(tmpString, ","));
            tmpString.clear();
        }
    }

    // запись в файл, из которого таблица была считана.
    // если таблица не была считана из файла, то будет ошибка!
    public void write() throws IOException
    {
        super.writeFile(file);
    }

    public void removeRow(Row r)
    {
        rows.remove(r);
    }

    // создать новую таблицу из выборки текущей таблицы
    // при вызове reBuildSubtables для родительской таблицы, второстепенная будет обновлена
    // больше ни какой связи между таблицами нет (кроме одинаковых объектов Row)
    public TSVTable createSubTable(Filter<Row> filter)
    {
        TSVTable ret = new TSVTable();

        buildSubtable(ret, filter);

        subTables.put(ret, filter);

        return ret;
    }

    public void reBuildSubtables()
    {
        for (Map.Entry<TSVTable, Filter<Row>> entry : subTables.entrySet())
        {
            buildSubtable(entry.getKey(), entry.getValue());
        }
    }

    private void buildSubtable(TSVTable subTable, Filter<Row> filter)
    {
        subTable.rows.clear();
        for (Row r : rows)
        {
            if (filter.accept(r))
                subTable.rows.add(r);
        }
        subTable.reBuildSubtables();
    }



}
