package ru.es.util;

import javolution.util.FastTable;

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

    public static <T, K> void addKToListInMap(Map<T, List<K>> map, T whereToAdd, K whatToAdd)
    {
        if (map.containsKey(whereToAdd))
        {
            map.get(whereToAdd).add(whatToAdd);
        }
        else
        {
            List<K> l = new ArrayList<>();
            l.add(whatToAdd);
            map.put(whereToAdd, l);
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
        List<K> replace = new FastTable<>();
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
        List<K> ticksToRemove = new FastTable<>();
        for (K l : notesInTicksStat.keySet())
        {
            if (!tmpMap.keySet().contains(l))
                ticksToRemove.add(l);
        }
        for (K l : ticksToRemove)
            notesInTicksStat.remove(l);

        // добавление новых тиков
        List<K> ticksToAdd = new FastTable<>();
        for (K l : tmpMap.keySet())
        {
            if (!notesInTicksStat.keySet().contains(l))
                ticksToAdd.add(l);
        }
        for (K l : ticksToAdd)
            notesInTicksStat.put(l, tmpMap.get(l));

        //Log.warning("MapUtils: modificate map. tmpMapSize: " + tmpMap.size() + ", replaced: " + replace.size() + ", added: " + ticksToAdd.size() + ", removed: " + ticksToRemove.size());
    }

}
