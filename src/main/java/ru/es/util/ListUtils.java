package ru.es.util;

import ru.es.log.Log;
import ru.es.math.ESMath;
import ru.es.math.Rnd;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javolution.util.FastTable;
import javolution.util.FastSet;

import java.util.*;

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
        List<Integer> ret = new FastTable<Integer>();

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
        List<Float> ret = new FastTable<Float>();

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
        List<Double> ret = new FastTable<Double>();

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
        List<String> ret = new FastTable<String>();
        for (String s : array)
        {
            ret.add(s);
        }
        return ret;
    }

    public static<T> List<T> arrayToList(T... array)
    {
        List<T> ret = new FastTable<>();
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


    public static<T> String arrayToString(T[] array)
    {
        List<T> list = arrayToList(array);
        return getStringFromList(list, ",");
    }

    public static List<String> getListOfString(String line, String separator)
    {
        List<String> ret = new FastTable<>();
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
        List<Boolean> ret = new FastTable<>();
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
        List<Long> ret = new FastTable<>();
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
        List<T> ret = new FastTable<>();
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

    public static<T> String LLtoString(List<List<T>> doubledList)
    {
        List<String> firstList = new FastTable<>();
        for (List<?> l : doubledList)
        {
            String str = ListUtils.getStringFromList(l, ",");
            firstList.add(str);
        }
        return ListUtils.getStringFromList(firstList, ";");
    }

    public static<T> String LStoString(List<Set<T>> doubledList)
    {
        List<String> firstList = new FastTable<>();
        for (Set<?> l : doubledList)
        {
            String str = ListUtils.getStringFromList(l, ",");
            firstList.add(str);
        }
        return ListUtils.getStringFromList(firstList, ";");
    }

    public static List<List<Long>> parseListOfListOfLong(String fromString)
    {
        //Log.warning("Count ; in "+fromString+" is: "+StringUtils.countSymbols(fromString, ";"));
        String[] first = fromString.split(";");
        List<List<Long>> ret = new FastTable<>();
        for (int i = 0; i < StringUtils.countSymbols(fromString, ";") + 1; i++)
        {
            ret.add(new FastTable<Long>());
        }

        int index = 0;
        for (String f : first)
        {
            f = f.trim();
            ret.get(index).addAll(getListOfLong(f, ","));
            index++;
        }
        return ret;
    }

    public static<T> String storeCollectionOfSet(Collection<Set<T>> doubledList)
    {
        List<String> firstList = new FastTable<>();
        for (Set<?> l : doubledList)
        {
            String str = ListUtils.getStringFromList(l, ",");
            firstList.add(str);
        }
        return ListUtils.getStringFromList(firstList, ";");
    }


    public static<T> List<Set<T>> parseListOfSet(String fromString, Class Tclass)
    {
        String[] first = fromString.split(";");
        List<Set<T>> ret = new FastTable<>();
        for (int i = 0; i < StringUtils.countSymbols(fromString, ";") + 1; i++)
        {
            ret.add(new FastSet<T>());
        }

        int index = 0;
        for (String f : first)
        {
            f = f.trim();
            ret.get(index).addAll(getList(f, ",", Tclass));
            index++;
        }
        return ret;
    }


    public static Object createInstance(String v, Class neadedClass)
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
        List<T> toRemove = new FastTable<>();
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

    public static<T> List<T> createList(T... items)
    {
        List<T> list = new FastTable<>();
        for (T t : items)
        {
            list.add(t);
        }
        return list;
    }

    public static<T> List<T> create(T... items)
    {
        List<T> list = new ArrayList<>();
        for (T t : items)
        {
            list.add(t);
        }
        return list;
    }


    public static List<Integer> createListI(int... items)
    {
        List<Integer> list = new FastTable<>();
        for (Integer t : items)
        {
            list.add(t);
        }
        return list;
    }

    public static<T> List<T> createList(Collection<T> items)
    {
        List<T> list = new FastTable<>();
        list.addAll(items);
        return list;
    }

    public static<T> ObservableList<T> createObservableList(T... items)
    {
        ObservableList<T> list = FXCollections.observableList(new FastTable<T>());
        for (T t : items)
        {
            list.add(t);
        }
        return list;
    }

    public static<T> List<T> createList(List<T> baseList, T... items)
    {
        List<T> list = new FastTable<>();
        list.addAll(baseList);
        list.addAll(Arrays.asList(items));
        return list;
    }


    public static<T> List<T> createList(List<T> items)
    {
        List<T> list = new FastTable<>();
        for (T t : items)
        {
            list.add(t);
        }
        return list;
    }

    public static<T> List<T> combine(List<T>... lists)
    {
        List<T> ret = new FastTable<T>();
        for (List<T> list : lists)
        {
            ret.addAll(list);
        }
        return ret;
    }

    public static<T> List<T> combine(List<T> list, T... etc)
    {
        List<T> ret = new FastTable<T>();
        ret.addAll(list);
        for (T e : etc)
        {
            ret.add(e);
        }
        return ret;
    }

    public static<T> Set<T> listToSet(List<T> list)
    {
        Set<T> ret = new FastSet<>();
        for (T t : list)
            ret.add(t);

        return ret;
    }

    public static<T> List<T> setToList(Set<T> set)
    {
        List<T> ret = new FastTable<>();
        for (T t : set)
            ret.add(t);

        return ret;
    }

    public static<K,V> void modificateMap(Map<K,V> modificable, Map<K,V> newMap)
    {
        modificable.putAll(newMap);
        List<K> toRemove = new FastTable<>();
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
        for (T bt : b)
        {
            ret.append(bt.toString() + delim);
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

}


