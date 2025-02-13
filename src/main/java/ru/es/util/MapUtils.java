package ru.es.util;


import ru.es.lang.Converter;

import java.util.*;

/**
 * Created by saniller on 23.07.2015.
 */
public class MapUtils
{
    public static <T> void addIntToIntMap(Map<T, Integer> map, T whereToAdd, int countToAdd)
    {
        if (map.containsKey(whereToAdd))
            map.put(whereToAdd, map.get(whereToAdd) + countToAdd);
        else
            map.put(whereToAdd, countToAdd);
    }

    // избавляет от лишней волокиты при добвлении объекта в список, который лежит в мапе
    public static <T, K> List<K> addKToListInMap(Map<T, List<K>> map, T whereToAdd, K whatToAdd)
    {
        if (map.containsKey(whereToAdd))
        {
            var list = map.get(whereToAdd);
            list.add(whatToAdd);
            return list;
        }
        else
        {
            List<K> list = new ArrayList<>();
            list.add(whatToAdd);
            map.put(whereToAdd, list);
            return list;
        }
    }

    public static <T> int getMaxIndex(Map<Integer, T> map)
    {
        if (map.isEmpty())
            return 0;
        int max = Integer.MIN_VALUE;
        for (int i : map.keySet())
        {
            if (i > max)
                max = i;
        }
        return max;
    }

    public static <T> int getMinIndex(Map<Integer, T> map)
    {
        if (map.isEmpty())
            return 0;
        int min = Integer.MAX_VALUE;
        for (int i : map.keySet())
        {
            if (i < min)
                min = i;
        }
        return min;
    }

    public static <K,V> void modificate(Map<K, V> notesInTicksStat, Map<K, V> tmpMap)
    {
        // проверка на соответствие
        List<K> replace = new ArrayList<>();
        for (K l : tmpMap.keySet())
        {
            if (notesInTicksStat.containsKey(l))
            {
                if (!notesInTicksStat.get(l).equals(tmpMap.get(l)))
                    replace.add(l);
            }
        }
        for (K l : replace)
            notesInTicksStat.put(l, tmpMap.get(l));

        // удаление несовпадающих тиков
        List<K> ticksToRemove = new ArrayList<>();
        for (K l : notesInTicksStat.keySet())
        {
            if (!tmpMap.keySet().contains(l))
                ticksToRemove.add(l);
        }
        for (K l : ticksToRemove)
            notesInTicksStat.remove(l);

        // добавление новых тиков
        List<K> ticksToAdd = new ArrayList<>();
        for (K l : tmpMap.keySet())
        {
            if (!notesInTicksStat.keySet().contains(l))
                ticksToAdd.add(l);
        }
        for (K l : ticksToAdd)
            notesInTicksStat.put(l, tmpMap.get(l));

        //Log.warning("MapUtils: modificate map. tmpMapSize: " + tmpMap.size() + ", replaced: " + replace.size() + ", added: " + ticksToAdd.size() + ", removed: " + ticksToRemove.size());
    }

    public static Map<Integer, Integer> parseMapOfInt(String data)
    {
        Map<Integer, Integer> map = new HashMap<>();

        if (data.isEmpty())
            return map;

        for (String entry : data.split(";"))
        {
            if (entry.isEmpty())
                continue;
            
            String[] entrtyArr = entry.split(",");
            int key = Integer.parseInt(entrtyArr[0]);
            int value = Integer.parseInt(entrtyArr[1]);
            map.put(key, value);
        }
        return map;
    }

    public static String mapOfIntToString(Map<Integer, Integer> map)
    {
        StringBuilder ret = new StringBuilder();
        boolean first = true;
        for (var e : map.entrySet())
        {
            if (!first)
                ret.append(";");
            else
                first = false;

            ret.append(e.getKey());
            ret.append(",");
            ret.append(e.getValue());
        }
        return ret.toString();
    }

    public static<Key, Item> Map<Key, List<Item>> createLists(List<Item> list, Converter<Item, Key> mapBy)
    {
        Map<Key, List<Item>> map = new HashMap<>();
        for (Item classId : list)
        {
            var key = mapBy.convert(classId);
            List<Item> addTo = map.get(key);
            if (addTo == null)
            {
                addTo = new ArrayList<>();
                map.put(key, addTo);
            }
            addTo.add(classId);
        }
        return map;
    }

    public static String toString(Map<String, String> parameters, String entryDelim, String entryKeyDelim, boolean quotes)
    {
        StringBuilder ret = new StringBuilder();
        boolean first = true;
        for (var e : parameters.entrySet())
        {
            if (first)
                first = false;
            else
            ret.append(entryDelim);

            ret.append(e.getKey());
            ret.append(entryKeyDelim);
            if (quotes)
                ret.append("\"");
            ret.append(e.getValue());
            if (quotes)
                ret.append("\"");
        }
        return ret.toString();
    }

    public static String toString(Map<String, String> parameters, String entryDelim, String entryKeyDelim, String quotes)
    {
        StringBuilder ret = new StringBuilder();
        boolean first = true;
        for (var e : parameters.entrySet())
        {
            if (first)
                first = false;
            else
            ret.append(entryDelim);

            ret.append(e.getKey());
            ret.append(entryKeyDelim);

            ret.append(quotes);
            ret.append(e.getValue());
            ret.append(quotes);
        }
        return ret.toString();
    }
}
