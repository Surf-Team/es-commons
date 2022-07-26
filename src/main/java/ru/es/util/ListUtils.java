package ru.es.util;

import javolution.util.FastSet;
import javolution.util.FastTable;
import ru.es.log.Log;
import ru.es.math.ESMath;
import ru.es.math.Rnd;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 03.09.14
 * Time: 23:45
 * To change this template use File | Settings | File Templates.
 */
public class ListUtils
{
    public static List<Integer> getListOfInt(String fromString)
    {
        List<Integer> ret = new ArrayList<>();

        StringTokenizer t = new StringTokenizer(fromString, ",");
        try
        {
            while (t.hasMoreTokens())
                ret.add(Integer.parseInt(t.nextToken()));
        }
        catch (Exception e)
        {
            Log.warning("Error getListOfInt. String is: " + fromString);
            e.printStackTrace();
        }

        return ret;
    }

    public static List<Float> getListOfFloat(String fromString)
    {
        List<Float> ret = new ArrayList<Float>();

        StringTokenizer t = new StringTokenizer(fromString, ",");
        try
        {
            while (t.hasMoreTokens())
                ret.add(Float.parseFloat(t.nextToken()));
        }
        catch (Exception e)
        {
            Log.warning("Error getListOfInt. String is: " + fromString);
            e.printStackTrace();
        }

        return ret;
    }

    public static List<Double> getListOfDouble(String fromString)
    {
        List<Double> ret = new ArrayList<>();

        StringTokenizer t = new StringTokenizer(fromString, ",");
        try
        {
            while (t.hasMoreTokens())
                ret.add(Double.parseDouble(t.nextToken()));
        }
        catch (Exception e)
        {
            Log.warning("Error getListOfFloat. String is: " + fromString);
            e.printStackTrace();
        }

        return ret;
    }


    public static Set<Integer> getSetOfInt(String fromString)
    {
        Set<Integer> ret = new HashSet<Integer>();

        StringTokenizer t = new StringTokenizer(fromString, ",");
        try
        {
            while (t.hasMoreTokens())
                ret.add(Integer.parseInt(t.nextToken()));
        }
        catch (Exception e)
        {
            Log.warning("Error getListOfInt. String is: " + fromString);
            e.printStackTrace();
        }

        return ret;
    }

    public static String getStringFromList(List<?> anyList, String separator)
    {
        StringBuilder b = new StringBuilder();
        int size = anyList.size();

        for (int i = 0 ; i < size; i++)
        {
            if (i != (size - 1))
                b.append(anyList.get(i) + separator);
            else
                b.append(anyList.get(i));
        }
        return b.toString();
    }

    public static<T> String getStringFromList(Set<T> anyList, String separator)
    {
        StringBuilder ret = new StringBuilder();
        int size = anyList.size();

        int i = 0;
        for (T t : anyList)
        {
            if (i != (size - 1))
                ret.append(t + separator);
            else
                ret.append(t);

            i++;
        }
        return ret.toString();
    }


    public static List<String> stringArrayToList(String[] array)
    {
        List<String> ret = new ArrayList<>();
        for (String s : array)
        {
            ret.add(s);
        }
        return ret;
    }

    public static<T> List<T> arrayToList(T... array)
    {
        List<T> ret = new ArrayList<>();
        for (T s : array)
        {
            ret.add(s);
        }
        return ret;
    }

    public static List<Integer> arrayToListInt(int[] array)
    {
        List<Integer> ret = new LinkedList<>();
        for (int s : array)
        {
            ret.add(s);
        }
        return ret;
    }

    public static<T> T[] listToArray(Collection<T> list, T[] array)
    {
        int index = 0;
        for (T s : list)
        {
            if (index < array.length) // иначе может быть ошибка при быстрой модификации списка
            {
                array[index] = s;
                index++;
            }
        }
        return array;
    }

    public static int[] listToArrayInt(Collection<Integer> list)
    {
        int[] array = new int[list.size()];
        int index = 0;
        for (int s : list)
        {
            if (index < array.length) // иначе может быть ошибка при быстрой модификации списка
            {
                array[index] = s;
                index++;
            }
        }
        return array;
    }


    public static<T> String arrayToString(T[] array)
    {
        List<T> list = arrayToList(array);
        return getStringFromList(list, ",");
    }

    public static<T> String arrayToStringRaw(Object array)
    {
        List list = new ArrayList();

        if (array != null)
        {
            for (int i = 0; i < Array.getLength(array); i++)
            {
                list.add(Array.get(array, i));
            }
        }

        return getStringFromList(list, ",");
    }

    public static List<String> getListOfString(String line, String separator)
    {
        List<String> ret = new ArrayList<>();
        StringTokenizer t = new StringTokenizer(line, separator);
        while (t.hasMoreTokens())
        {
            String nt = t.nextToken();
            if (!nt.trim().isEmpty())
                ret.add(nt);
        }
        return ret;
    }

    public static List<Boolean> getListOfBoolean(String fullString, String separator)
    {
        List<Boolean> ret = new ArrayList<>();
        StringTokenizer t = new StringTokenizer(fullString, separator);
        while (t.hasMoreTokens())
        {
            String nt = t.nextToken();
            if (!nt.trim().isEmpty())
                ret.add(Boolean.parseBoolean(nt));
        }
        return ret;
    }

    public static List<Long> getListOfLong(String fullString, String separator)
    {
        List<Long> ret = new ArrayList<>();
        StringTokenizer t = new StringTokenizer(fullString, separator);
        while (t.hasMoreTokens())
        {
            String nt = t.nextToken();
            if (!nt.trim().isEmpty())
                ret.add(Long.parseLong(nt));
        }
        return ret;
    }

    public static<T> List<T> getList(String fullString, String separator, Class Tclass)
    {
        List<T> ret = new ArrayList<>();
        StringTokenizer t = new StringTokenizer(fullString, separator);
        while (t.hasMoreTokens())
        {
            String nt = t.nextToken().trim();
            if (!nt.isEmpty())
            {
                T instance = (T) createInstance(nt, Tclass);
                ret.add(instance);
            }
        }
        return ret;
    }
    

    private static Object createInstance(String v, Class neadedClass)
    {
        if (neadedClass == Boolean.class)
            return Boolean.parseBoolean(v);
        else if (neadedClass == Integer.class)
            return Integer.parseInt(v);
        else if (neadedClass == Double.class)
            return Double.parseDouble(v);
        else if (neadedClass == Long.class)
            return Long.parseLong(v);
        else if (neadedClass == String.class)
            return v;
        else
            Log.warning("Error in list utils! Create instance of " + v + " is not "+neadedClass.getName());

        return null;
    }

    public static<T> boolean setsEquals(Set<T> a, Set<T> b)
    {
        if (a == b)
            return true;
        if (a.size() != b.size())
            return false;

        int containsCount = 0;
        for (T i : a)
        {
            if (b.contains(i))
                containsCount++;
        }
        if (b.size() == containsCount)
            return true;

        return false;
    }


    public static<T> boolean containsEqual(Collection<T> list, T containsThis)
    {
        for (T t : list)
        {
            if (t.equals(containsThis))
                return true;
        }
        return false;
    }

    public static<T> void removeEquals(Collection<T> list, T containsThis)
    {
        List<T> toRemove = new ArrayList<>();
        for (T t : list)
        {
            if (t.equals(containsThis))
                toRemove.add(t);
        }
        list.removeAll(toRemove);
    }

    public static<T> boolean listEquals(Collection<T> listA, Collection<T> listB)
    {
        if (listA == null || listB == null)
            return false;
        if (listA == listB)
            return true;
        if (listA.size() != listB.size())
            return false;

        // old logic
        /**for (T i : listA)
        {
            if (!listB.contains(i))
                return false;
        }
        for (T i : listB)
        {
            if (!listA.contains(i))
                return false;
        }  **/

        for (T n : listA)
        {
            boolean contains = false;
            for (T n2 : listB)
            {
                if (n.equals(n2))
                    contains = true;
            }

            if (!contains)
                return false;

        }

        return true;
    }

    public static<T> T getPreviusOrAnyItemFromListExcept(List<T> items, T except)
    {
        int indexE = items.indexOf(except);
        if (indexE > 0)
        {
            return items.get(indexE-1);
        }
        else
        {
            return items.get(1);
        }
    }

    public static<T> void modificateList(List<T> list, List<T> updateByThis, T... alwaysFirst)
    {
        for (T t : alwaysFirst)
        {
            updateByThis.add(0, t);
        }

        List<T> toRemove = new ArrayList<>();
        List<T> toAdd = new ArrayList<>();
        for (T t : list)
        {
            if (!updateByThis.contains(t))  // если в новом списке нет элементов старого
                toRemove.add(t);
        }
        for (T t : updateByThis)
        {
            if (!list.contains(t))
                toAdd.add(t);
        }
        list.removeAll(toRemove);
        list.addAll(toAdd);
    }

    public static<T> void modificateCollection(Collection<T> list, Collection<T> updateByThis)
    {
       // list.retainAll(updateByThis);

        List<T> toRemove = new ArrayList<>();
        List<T> toAdd = new ArrayList<>();
        for (T t : list)
        {
            if (!updateByThis.contains(t))  // если в новом списке нет элементов старого
                toRemove.add(t);
        }
        for (T t : updateByThis)
        {
            if (!list.contains(t))
                toAdd.add(t);
        }
        if (!toRemove.isEmpty())
            list.removeAll(toRemove);
        if (!toAdd.isEmpty())
            list.addAll(toAdd);
    }


    public static List<Integer> createListI(int... items)
    {
        List<Integer> list = new ArrayList<>();
        for (Integer t : items)
        {
            list.add(t);
        }
        return list;
    }

    public static<T> List<T> createList(Collection<T> items)
    {
        return new ArrayList<>(items);
    }

    public static <T> List<T> createListFromArr(T[] items)
    {
        ArrayList<T> ret = new ArrayList<>();
        for (T t : items)
        {
            ret.add(t);
        }

        return ret;
    }


    public static<T> List<T> createList(List<T> baseList, T... items)
    {
        List<T> list = new ArrayList<>();
        list.addAll(baseList);
        list.addAll(Arrays.asList(items));
        return list;
    }


    public static<T> List<T> createList(List<T> items)
    {
        List<T> list = new ArrayList<>();
        if (items != null)
            list.addAll(items);

        return list;
    }

    public static<T> List<T> createList(T... items)
    {
        List<T> list = new ArrayList<>();
        for (T t : items)
            list.add(t);
        return list;
    }

    public static<T> List<T> createListFromArrays(T[]... items)
    {
        List<T> list = new ArrayList<>();
        for (T[] tt : items)
        {
            for (T t : tt)
            {
                list.add(t);
            }
        }
        return list;
    }

    public static<T> List<T> combine(List<T>... lists)
    {
        List<T> ret = new ArrayList<T>();
        for (List<T> list : lists)
        {
            ret.addAll(list);
        }
        return ret;
    }

    public static<T> List<T> combine(List<T> list, T... etc)
    {
        List<T> ret = new ArrayList<>();
        ret.addAll(list);
        for (T e : etc)
        {
            ret.add(e);
        }
        return ret;
    }
    
    public static<K,V> void modificateMap(Map<K,V> modificable, Map<K,V> newMap)
    {
        modificable.putAll(newMap);
        List<K> toRemove = new ArrayList<>();
        for (K k : modificable.keySet())
        {
            if (!newMap.containsKey(k))
                toRemove.add(k);
        }
        for (K k : toRemove)
        {
            modificable.remove(k);
        }
    }

    public static<T> boolean containsAny(Collection<T> a, Collection<T> b)
    {
        for (T t : a)
        {
            if (b.contains(t))
                return true;
        }
        return false;
    }

    public static<T> void moveElement(List<T> collection, T element, boolean increment)
    {
        int minIndex = 0;
        int maxIndex = collection.size() - 1;
        int currentIndex = collection.indexOf(element);

        int add = 1;
        if (!increment)
            add = -1;

        int newIndex = ESMath.constrain(currentIndex +add, minIndex, maxIndex);
        collection.remove(element);
        collection.add(newIndex, element);
    }


    public static<T> void removeRndWithPriority(int count, Collection<T> l, T... except)
    {
        if (count < 0)
            return;

        List<T> priorityRemove = new ArrayList<T>();
        priorityRemove.addAll(l);
        for (T t : except)
            priorityRemove.remove(t);

        for (int i = 0; i < count; i++)
        {
            if (priorityRemove.size() > 0 && l.size() > 0)
            {
                T rem = Rnd.getRndFromList(priorityRemove);
                priorityRemove.remove(rem);
                l.remove(rem);
            }
            else if (l.size() > 0)
            {
                T rem = Rnd.getRndFromCollection(l);
                l.remove(rem);
            }
        }
    }

    public static<T> void limitRndRemoveOver(int maxCount, Collection<T> l, T... except)
    {
        if (maxCount < 0)
            return;

        List<T> priorityRemove = new ArrayList<T>();
        priorityRemove.addAll(l);
        for (T t : except)
            priorityRemove.remove(t);

        while (l.size() - maxCount > 0)
        {
            if (priorityRemove.size() > 0)
            {
                T rem = Rnd.getRndFromList(priorityRemove);
                priorityRemove.remove(rem);
                l.remove(rem);
            }
            else
            {
                T rem = Rnd.getRndFromCollection(l);
                l.remove(rem);
            }
        }
    }

    public static<T> List<T> getUnique(Collection<T>... fromLists)
    {
        List<T> ret = new ArrayList<>();
        for (Collection<T> etc : fromLists)
        {
            for (T i : etc)
            {
                if (!ret.contains(i))
                    ret.add(i);
            }
        }
        return ret;
    }

    public static<T> boolean containsAll(List<T> where, List<T> what)
    {
        for (T w : what)
        {
            if (!where.contains(w))
                return false;
        }
        return true;
    }

    public static Integer getMin(List<Integer> list)
    {
        Integer ret = null;
        for (Integer i : list)
        {
            if(ret == null)
                ret = i;
            else
            {
                if (i < ret)
                    ret = i;
            }
        }
        return ret;
    }

    public static Integer getMax(List<Integer> list)
    {
        Integer ret = null;
        for (Integer i : list)
        {
            if(ret == null)
                ret = i;
            else
            {
                if (i > ret)
                    ret = i;
            }
        }
        return ret;
    }

    public static void transposition(List<Integer> accord, int size)
    {
        if (size == 0)
            return;
        
        while (size != 0)
        {
            if (size > 0)
            {
                size--;

                Integer min = getMin(accord);
                accord.remove(min);
                accord.add(min+12);
            }
            else
            {
                size++;

                Integer max = getMax(accord);
                accord.remove(max);
                accord.add(max-12);
            }
        }
    }

    public static<T> String toString(List<T> b, String delim)
    {
        StringBuilder ret = new StringBuilder();

        boolean first = true;

        for (T bt : b)
        {
            if (first)
                first = false;
            else
                ret.append(delim);
            
            ret.append(bt.toString());
        }
        return ret.toString();
    }

    public static int getAbsoluteSum(List<Integer> list, int offset)   // offset is center
    {
        int sum = 0;
        for (Integer i : list)
        {
            sum += Math.abs(i - offset);
        }
        return sum;
    }

    public static<T> void addAll(Collection<T> to, T... objects)
    {
        for (T t : objects)
            to.add(t);
    }

    public static<T> void addAll(Collection<T> to, Collection<T> objects)
    {
        for (T t : objects)
            to.add(t);
    }

    public static<T> ListChanges<T> getChanges(Collection<T> presentList, Collection<T> oldList)
    {
        ListChanges<T> ret = new ListChanges<>();

        for (T dp : presentList)
        {
            if (!oldList.contains(dp))
                ret.added.add(dp);
        }
        for (T dp : oldList)
        {
            if (!presentList.contains(dp))
                ret.removed.add(dp);
        }

        return ret;
    }

    public static<T> int containsIn(List<List<T>> lists, T object)
    {
        for (List<T> list : lists)
        {
            if (list.contains(object))
                return lists.indexOf(list);
        }

        return -1;
    }

    public static<T> void removeFrom(List<List<T>> lists, T object)
    {
        for (List<T> list : lists)
        {
            list.remove(object);
        }
    }

    public static<T> List<T> getListWithLowerItems(List<List<T>> lists)
    {
        List<T> ret = null;
        int lowerItems = Integer.MAX_VALUE;

        for (List<T> list : lists)
        {
            if (list.size() < lowerItems)
            {
                lowerItems = list.size();
                ret = list;
            }
        }
        return ret;
    }

	public static int[] sortByPoints(List<?> teams, int[] points, boolean smallFirst)
	{
        var teamsSorted = ListUtils.createList(teams);
        teamsSorted.sort(new Comparator() {
            @Override
            public int compare(Object o1, Object o2)
            {
                int indexOf1 = teams.indexOf(o1);
                int indexOf2 = teams.indexOf(o2);

                int points1 = points[indexOf1];
                int points2 = points[indexOf2];

                if (smallFirst)
                    return points1 - points2;
                else
                    return points2 - points1;
            }
        });

        int[] ret = new int[points.length];

        for (int i = 0; i < ret.length; i++)
        {
            ret[i] = teams.indexOf(teamsSorted.get(i));
        }

        return ret;
	}

    public static void removeEmptyStrings(List<String> strings)
    {
        List<String> toRemove = new ArrayList<>();
        for (String s : strings)
        {
            if (s.trim().isEmpty())
                toRemove.add(s);;
        }
        strings.removeAll(toRemove);
    }

    public static class ListChanges<T>
    {
        public final List<T> added = new ArrayList<>();
        public final List<T> removed = new ArrayList<>();
    }

    public static<T> List<T> concurrentList()
    {
        return new FastTable<>();
    }

    public static<T> List<T> createConcurrentList(T... items)
    {
        List<T> ret = concurrentList();
        ret.addAll(Arrays.asList(items));
        return ret;
    }

    public static<T> Set<T> concurrentSet()
    {
        return new FastSet<T>();
    }

}


