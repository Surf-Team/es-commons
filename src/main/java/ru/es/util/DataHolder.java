package ru.es.util;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * 18.04.2025 - 12:58
 */
public class DataHolder<T>
{
    private final ArrayList<ArrayList<T>> data;

    public DataHolder(String input, Function<String, T> parser)
    {
        data = new ArrayList<>();
        String[] rows = input.split(";");

        for (String row : rows)
        {
            ArrayList<T> newRow = new ArrayList<>();
            String[] cols = row.split(",");

            for (String col : cols)
            {
                newRow.add(parser.apply(col.trim()));
            }
            data.add(newRow);
        }
    }

    public ArrayList<ArrayList<T>> getData()
    {
        return data;
    }

    public void display()
    {
        for (ArrayList<T> row : data)
        {
            for (T value : row)
            {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }
}
