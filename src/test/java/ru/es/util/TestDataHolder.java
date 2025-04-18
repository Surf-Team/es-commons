package ru.es.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * 18.04.2025 - 16:44
 */
public class TestDataHolder
{
    @Test
    public void testDataHolder() throws Exception
    {
        DataHolder<Integer> integerTwoDataHolder = new DataHolder<>("1,2;3,4", Integer::parseInt);
        ArrayList<ArrayList<Integer>> integerTwoData = integerTwoDataHolder.getData();
        Assertions.assertFalse(integerTwoData.isEmpty());

        for (ArrayList<Integer> integers : integerTwoData)
        {
            Assertions.assertEquals(Integer.class, integers.get(0).getClass());
            Assertions.assertEquals(2, integers.size());
        }

        DataHolder<Integer> integerThreeDataHolder = new DataHolder<>("1,2,3;4,5,6", Integer::parseInt);
        ArrayList<ArrayList<Integer>> integerThreeData = integerThreeDataHolder.getData();
        Assertions.assertFalse(integerThreeData.isEmpty());

        for (ArrayList<Integer> integers : integerThreeData)
        {
            Assertions.assertEquals(Integer.class, integers.get(0).getClass());
            Assertions.assertEquals(3, integers.size());
        }

        DataHolder<String> stringDataHolder = new DataHolder<>("text1,text2;text3,text4", String::trim);
        ArrayList<ArrayList<String>> stringData = stringDataHolder.getData();
        Assertions.assertFalse(stringData.isEmpty());

        for (ArrayList<String> strings : stringData)
        {
            Assertions.assertEquals(String.class, strings.get(0).getClass());
            Assertions.assertEquals(2, strings.size());
        }


        DataHolder<Double> doubleDataHolder = new DataHolder<>("1.1,2.2;3.3,4.4", Double::parseDouble);
        ArrayList<ArrayList<Double>> doubleData = doubleDataHolder.getData();
        Assertions.assertFalse(doubleData.isEmpty());

        for (ArrayList<Double> doubles : doubleData)
        {
            Assertions.assertEquals(Double.class, doubles.get(0).getClass());
            Assertions.assertEquals(2, doubles.size());
        }
    }
}
