package ru.es.util;

import java.util.*;


public class SortUtils
{
    public static List<Integer> sortIntegers(Collection<Integer> integerList)
    {
        List<Integer> sortedList = new ArrayList<>();
        for (Integer n : integerList)
        {
            int index = 0;
            if (sortedList.isEmpty())
                sortedList.add(n);
            else
            {
                boolean added = false;
                for (int inSorted : sortedList)
                {
                    if (n < inSorted)
                    {
                        sortedList.add(index, n);
                        added = true;
                        break;
                    }
                    index++;
                }
                if (!added)
                    sortedList.add(n);
            }
        }
        return sortedList;
    }

    public static List<Long> sortLongs(Collection<Long> longList)
    {
        List<Long> sortedList = new ArrayList<>();
        for (long n : longList)
        {
            int index = 0;
            if (sortedList.isEmpty())
                sortedList.add(n);
            else
            {
                boolean added = false;
                for (long inSorted : sortedList)
                {
                    if (n < inSorted)
                    {
                        sortedList.add(index, n);
                        added = true;
                        break;
                    }
                    index++;
                }
                if (!added)
                    sortedList.add(n);
            }
        }
        return sortedList;
    }

    public static<K> List sortByValue(Map<K, Integer> map, boolean smallFirst)
    {
        List<K> ret = new ArrayList<>();
        List<Integer> tempInt = new ArrayList<>();

        for (Map.Entry<K, Integer> n : map.entrySet())
        {
            int value = n.getValue();
            int index = 0;
            if (ret.isEmpty())
            {
                ret.add(n.getKey());
                tempInt.add(value);
            }
            else
            {
                boolean added = false;
                for (int alreadyInList : tempInt)
                {
                    if (smallFirst)
                    {
                        if (value <= alreadyInList)
                        {
                            ret.add(index, n.getKey());
                            tempInt.add(index, value);
                            added = true;
                            break;
                        }
                    }
                    else
                    {
                        if (value >= alreadyInList)
                        {
                            ret.add(index, n.getKey());
                            tempInt.add(index, value);
                            added = true;
                            break;
                        }
                    }
                    index++;
                }
                if (!added)
                {
                    ret.add(n.getKey());
                    tempInt.add(value);
                }
            }
        }

        return ret;
    }

    public static<K> List<K> sortByValueF(Map<K, Float> map, boolean smallFirst)
    {
        List<K> ret = new ArrayList<>();
        List<Float> tempInt = new ArrayList<>();

        for (Map.Entry<K, Float> n : map.entrySet())
        {
            float value = n.getValue();
            int index = 0;
            if (ret.isEmpty())
            {
                ret.add(n.getKey());
                tempInt.add(value);
            }
            else
            {
                boolean added = false;
                for (float alreadyInList : tempInt)
                {
                    if (smallFirst)
                    {
                        if (value <= alreadyInList)
                        {
                            ret.add(index, n.getKey());
                            tempInt.add(index, value);
                            added = true;
                            break;
                        }
                    }
                    else
                    {
                        if (value >= alreadyInList)
                        {
                            ret.add(index, n.getKey());
                            tempInt.add(index, value);
                            added = true;
                            break;
                        }
                    }
                    index++;
                }
                if (!added)
                {
                    ret.add(n.getKey());
                    tempInt.add(value);
                }
            }
        }

        return ret;
    }


    public static<K> List sortByDoubleValue(Map<K, Double> map, boolean smallFirst)
    {
        List<K> ret = new ArrayList<>();
        List<Double> tempInt = new ArrayList<>();

        for (Map.Entry<K, Double> n : map.entrySet())
        {
            double value = n.getValue();
            int index = 0;
            if (ret.isEmpty())
            {
                ret.add(n.getKey());
                tempInt.add(value);
            }
            else
            {
                boolean added = false;
                for (double alreadyInList : tempInt)
                {
                    if (smallFirst)
                    {
                        if (value <= alreadyInList)
                        {
                            ret.add(index, n.getKey());
                            tempInt.add(index, value);
                            added = true;
                            break;
                        }
                    }
                    else
                    {
                        if (value >= alreadyInList)
                        {
                            ret.add(index, n.getKey());
                            tempInt.add(index, value);
                            added = true;
                            break;
                        }
                    }
                    index++;
                }
                if (!added)
                {
                    ret.add(n.getKey());
                    tempInt.add(value);
                }
            }
        }

        return ret;
    }

    public static void sortStrings(String[] strings)
    {
        Arrays.sort(strings, String.CASE_INSENSITIVE_ORDER);
    }

}
