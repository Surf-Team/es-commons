package ru.es.math;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.util.SortUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 05.10.14
 * Time: 20:05
 * To change this template use File | Settings | File Templates.
 */
public class ESMath
{
    // во сколько раз одно больше другого
    // если good больше, то знак плюс, если evil больше, то знак минус
    public static double equality(int good, int evil)
    {
        if (good == evil)
            return 0;
        else
        {
            if (good > evil)
                return (double) good / evil;
            else
                return (double) evil / good * -1;
        }
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static int constrain(int value, int min, int max)
    {
        if (value < min)
            return min;

        if (value > max)
            return max;

        return value;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static long constrain(long value, long min, long max)
    {
        if (value < min)
            return min;

        if (value > max)
            return max;

        return value;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double constrain(double value, double min, double max)
    {
        if (value < min)
            return min;

        if (value > max)
            return max;

        return value;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float constrain(float value, float min, float max)
    {
        if (value < min)
            return min;

        if (value > max)
            return max;

        return value;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static int min(int value1, int value2)
    {
        if (value1 > value2)
            return value2;
        else
            return value1;
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double min(double value1, double value2)
    {
        if (value1 > value2)
            return value2;
        else
            return value1;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float min(float value1, float value2)
    {
        if (value1 > value2)
            return value2;
        else
            return value1;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static long min(long value1, long value2)
    {
        if (value1 > value2)
            return value2;
        else
            return value1;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static int max(int value1, int value2)
    {
        if (value1 > value2)
            return value1;
        else
            return value2;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double max(double value1, double value2)
    {
        if (value1 > value2)
            return value1;
        else
            return value2;
    }
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float max(float value1, float value2)
    {
        if (value1 > value2)
            return value1;
        else
            return value2;
    }
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static long max(long value1, long value2)
    {
        if (value1 > value2)
            return value1;
        else
            return value2;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static int square(int num)
    {
        return num*num;
    }

    public static List<Integer> intRange(int min, int max)
    {
        List<Integer> ret = new LinkedList<>();
        for (int i = min; i <= max; i++)
        {
            ret.add(i);
        }
        return ret;
    }

    public static List<Integer> intRangeExceptMax(int min, int max)
    {
        List<Integer> ret = new LinkedList<>();
        for (int i = min; i < max; i++)
        {
            ret.add(i);
        }
        return ret;
    }

    public static int getZeroCount(int num)
    {
        // только положительные!
        // 1 = 1, 0 = 1, 9 = 1;
        // 10 = 2, 15 = 2, 53 = 2, 99 = 2;
        // 100 = 3
        // ...
        return (""+num).length();
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static int getPointsAfterZero(double number)
    {
        String txt = (""+number);
        return txt.length() - txt.indexOf(".") - 1;
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double round(double num, int zeroCount)
    {
        return new BigDecimal(num).setScale(zeroCount, RoundingMode.UP).doubleValue();
    }

    // округление например до 0.05. Т.е. значение может быть только к примеру 315135.30 или 4363.55. Т.е. минимальный шаг - 0.5
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double specialRound(double num, double step)
    {
        //if (step >= 1.0)
            //return Math.round(num);

        long newVal = Math.round(num/step);
        double newDoubleVal = (double) newVal;
        //Log.warning("newDoubleVal: "+newDoubleVal);

        double ret = newDoubleVal *step;

        String newRet = getNumberWithFixedSizeAfterDot(ret, getPointsAfterZero(step));
        ret = Double.parseDouble(newRet);


        return ret;
    }

    // просто создаём нули для какой либо цели
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static String createZeros(int count)
    {
        String ret = "";
        for (int i = 0; i < count; i++)
        {
            ret+="0";
        }
        return ret;
    }

    // возвращаем строку из числа double с фиксированным количеством знаков после запятой
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static String getNumberWithFixedSizeAfterDot(double num, int fixedZeroCount)
    {
        String retString = ""+num;
        int pointIndex = retString.indexOf(".");

        // часть после точки
        String afterDot = retString.substring(pointIndex+1, min(pointIndex+1+fixedZeroCount, retString.length()));
        if (afterDot.length() < fixedZeroCount)
            afterDot+= createZeros(fixedZeroCount - afterDot.length());

        String newRet = retString.substring(0, pointIndex)+"."+afterDot;
        return newRet;
    }

    public static boolean isInInterval(int value, int min, int max)
    {
        if (value >= min && value <= max)
            return true;
        else
            return false;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static List<Integer> getHarmony(List<Integer> items, int octaveSize)
    {
        List<Integer> ret = new ArrayList<>();

        for (int i : items)
        {
            int n = i % octaveSize;
            if (!ret.contains(n))
                ret.add(n);
        }

        return ret;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static List<Integer> getParrallels(List<Integer> harmony, int octave, int maxOctaves)
    {
        List<Integer> allPressedNotes = new LinkedList<>();
        for (int i = 0; i < harmony.size(); i++)
        {
            int realNote = harmony.get(i);

            int noteInOctave = realNote % octave;
            if (!allPressedNotes.contains(noteInOctave))
                for (int k = 0; k < maxOctaves; k++)
                {
                    allPressedNotes.add(noteInOctave + octave * k);
                }
        }
        allPressedNotes = SortUtils.sortIntegers(allPressedNotes);
        return allPressedNotes;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static List<Integer> getParrallels(Set<Integer> collection, int split, int maxParrallels)
    {
        List<Integer> allPressedNotes = new LinkedList<>();
        for (int realNote : collection)
        {
            int noteInOctave = realNote % split;
            if (!allPressedNotes.contains(noteInOctave))
                for (int k = 0; k < maxParrallels; k++)
                {
                    allPressedNotes.add(noteInOctave + split * k);
                }
        }
        allPressedNotes = SortUtils.sortIntegers(allPressedNotes);
        return allPressedNotes;
    }

    // перевод из линейного значения параметра (например 0-127) в нелинейное, например 0.001 - 45.0)
    // min - минимальное значение на выходе (например 0.001)
    // max - максимальное значение на выходе (например 45.0)
    // maxInput - максимальное значение на входе (127 например). Соответственно минимальное всегда 0.
    // pow - степень. Если больше нуля - то график прогнутый вниз (маленькие значения более подробны, большие значения наоборот). Например 4.
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float linearToPowed(float minOutput, float maxOutput, int maxInput, int input, float pow)
    {
        return (float) (minOutput + (((maxOutput - minOutput) / Math.pow(maxInput, pow)) * (Math.pow(input, pow))));
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float linearToPowed(float minOutput, float maxOutput, float maxInput, float input, float pow)
    {
        return (float) (minOutput + (((maxOutput - minOutput) / Math.pow(maxInput, pow)) * (Math.pow(input, pow))));
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double linearToPowed(double minOutput, double maxOutput, double maxInput, double input, double pow)
    {
        return minOutput + ((maxOutput - minOutput) * (Math.pow(input, pow))) / Math.pow(maxInput, pow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double linearToPowedNormalized(double input, double maxOutput, double pow)
    {
        return Math.pow(input, pow) * maxOutput;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float linearToPowedNormalized(float input, float maxOutput, float pow)
    {
        return (float) Math.pow(input, pow) * maxOutput;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float linearToPowedNormalized(float input, float pow)
    {
        return (float) Math.pow(input, pow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float linearToPowedNormalizedWithPolarity(float input, float maxOutput, float pow)
    {
        if (input > 0)
        {
            input = Math.abs(input);
            return (float) Math.pow(input, pow) * maxOutput;
        }
        else
        {
            input = Math.abs(input);
            return (float) Math.pow(input, pow) * maxOutput * -1;
        }
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float linearToPowedNormalizedWithPolarity(float input, float pow)
    {
        if (input > 0)
        {
            input = Math.abs(input);
            return (float) Math.pow(input, pow);
        }
        else
        {
            input = Math.abs(input);
            return (float) Math.pow(input, pow) * -1;
        }
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double linearToPowed(double minOutput, double maxOutput, double minInput, double maxInput, double input, double pow)
    {
        return minOutput + ((maxOutput - minOutput) * (Math.pow(input-minInput, pow))) / Math.pow(maxInput-minInput, pow);
    }
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double linearToPowedNormalized(double minInput, double maxInput, double input, double pow)
    {
        return ((Math.pow(input-minInput, pow))) / Math.pow(maxInput-minInput, pow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float linearToPowed(float minOutput, float maxOutput, float minInput, float maxInput, float input, float pow)
    {
        return minOutput + ((maxOutput - minOutput) * ((float) Math.pow(input-minInput, pow))) / (float) Math.pow(maxInput-minInput, pow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double transferValue(double minOutput, double maxOutput, double minInput, double maxInput, double input, double pow)
    {
        return constrain((minOutput + ((maxOutput - minOutput) * (Math.pow(input-minInput, pow))) / Math.pow(maxInput-minInput, pow)), minOutput, maxOutput);
    }
                                          // аргументы в том же порядке. Изменён только инпут
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double powedToLinear(double min, double max, double maxInput, double powedValue, double pow)
    {
        powedValue -= min;
        powedValue *= Math.pow(maxInput, pow); // pow или powed value
        powedValue /= (max-min);

        return Math.pow(powedValue, 1/pow);//ESMath.log(powedValue, pow);
    }
                                          // аргументы в том же порядке. Изменён только инпут

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float powedToLinear(float min, float max, float maxInput, float powedValue, float pow)
    {
        powedValue -= min;
        powedValue *= Math.pow(maxInput, pow); // pow или powed value
        powedValue /= (max-min);

        return (float) Math.pow(powedValue, 1/pow);//ESMath.log(powedValue, pow);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double log(double a, double b)
    {
        return Math.log(b) / Math.log(a);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double limitedChange(double value, double changeTo, double limitChange)
    {
        if (value - changeTo > limitChange)
        {
            changeTo = value - limitChange;
        }
        else if (changeTo - value > limitChange)
        {
            changeTo = value + limitChange;
        }
        return changeTo;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float limitedChange(float value, float changeTo, float limitChange)
    {
        if (value - changeTo > limitChange)
        {
            return value - limitChange;
        }
        else if (changeTo - value > limitChange)
        {
            return value + limitChange;
        }
        return changeTo;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static int[] getNeighbourds(List<Integer> numbers, int base)
    {
        int upperNeighbour = Integer.MAX_VALUE;
        int lowerNeighbour = Integer.MIN_VALUE;
        for (int i = 0; i < numbers.size(); i++)
        {
            int gammaNote = numbers.get(i);

            if (gammaNote > base && gammaNote < upperNeighbour)
                upperNeighbour = gammaNote;
            if (gammaNote < base && gammaNote > lowerNeighbour)
                lowerNeighbour = gammaNote;
        }
        return new int[] {lowerNeighbour, upperNeighbour};
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static int[] getNeighbourds(Set<Integer> numbers, int base)
    {
        int upperNeighbour = Integer.MAX_VALUE;
        int lowerNeighbour = Integer.MIN_VALUE;
        for (int gammaNote : numbers)
        {
            if (gammaNote > base && gammaNote < upperNeighbour)
                upperNeighbour = gammaNote;
            if (gammaNote < base && gammaNote > lowerNeighbour)
                lowerNeighbour = gammaNote;
        }
        return new int[] {lowerNeighbour, upperNeighbour};
    }

    // return example: maxIn: 127
    // value: 64 -> { 0,0 };
    // value: 127 -> { 0,1 };
    // value: 0 -> { 1,0 };
    // value: 32 -> { 0.5, 0 };
    // value: 96 -> { 0, 0.5 };

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double[] splitValuesByCenter(double value, double maxInput)
    {
        double center = maxInput / 2.0;
        return new  double[]
                {
                        (maxInput-(ESMath.min(value, center)*2)),
                        ((ESMath.max(center, value)-center)*2)
                };
    }

    // result: 0 to 1
    // val0 (min)
    // val1 (max)

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float intermediate(float val0, float val1, float val, float pow)
    {
        if (pow == 1)
            return ESMath.constrain((val - val0) / (val1 - val0), 0, 1);
        else
            return linearToPowedNormalized(ESMath.constrain((val - val0) / (val1 - val0), 0, 1), pow);
    }

    // velo mapping: (velo, 35, 100) = 0 при 35, 1 при 100.

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static float mappingConstrained(float inputValue, float zeroVal, float fullVal)
    {
        if (inputValue < zeroVal)
            return 0f;
        if (inputValue > fullVal)
            return 1f;

        inputValue -= zeroVal;

        return inputValue / (fullVal - zeroVal);
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double divRemains(double c, double perc) // % 2 Но только цезые числа
    {
        c += perc;
        c/= perc;
        c = c - (int) c;
        c*= perc;
        return perc;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static long quantize(int initValue, int quantize, int upMod)
    {
        if (quantize == 0)
            return initValue;

        long pointTick = initValue + upMod;
        return pointTick - pointTick % quantize;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static long quantize(int initValue, int quantize)
    {
        if (quantize == 0)
            return initValue;

        int upMod = quantize / 2;
        long pointTick = initValue + upMod;
        return pointTick - pointTick % quantize;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    @Slow
    // долгий метод! Т.к. используется % с Double!
    public static double quantize(double initValue, double quantize)
    {
        if (quantize == 0.0)
            return initValue;

        double upMod = quantize / 2.0;
        double pointTick = initValue + upMod;
        return pointTick - pointTick % quantize;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    @Slow
    // долгий метод! Т.к. используется % с Float!
    public static float quantize(float initValue, float quantize)
    {
        if (quantize == 0f)
            return initValue;

        float upMod = quantize / 2f;
        float pointTick = initValue + upMod;
        return pointTick - pointTick % quantize;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static long quantizeToMin(int initValue, int quantize)
    {
        if (quantize == 0)
            return  initValue;
        return initValue - initValue % quantize;
    }


    // used for layout to adjust widths to honor the min/max policies consistently
    public static double boundedSize(double value, double min, double max) {
        // if max < value, return max
        // if min > value, return min
        // if min > max, return min
        return Math.min(Math.max(value, min), Math.max(min,max));
    }

    // ищет следующее число от from, кратное each, но не ближе minDistance к from
    public static long nextAlignedNumber(long from, long each, long minDistance)
    {
        long millisLeft = each - (from % (each));

        long floorMult = 0;
        if (millisLeft < minDistance)
            floorMult = minDistance / each + 1;

        //Log.warning("floorMult: "+floorMult);
        return millisLeft + (floorMult*each);
    }

    public static int getMinFrom(int[] values)
    {
        int min = Integer.MAX_VALUE;
        for (int val : values)
        {
            if (val < min)
                min = val;
        }
        return min;
    }

    public static int getMaxFrom(int[] values)
    {
        int max = Integer.MIN_VALUE;
        for (int val : values)
        {
            if (val > max)
                max = val;
        }
        return max;
    }

    // разрезаем число на части
    // 10, 3 вернёт int[] { 4, 3, 3 }
    // 11, 3 вернёт int[] { 4, 4, 3 }
    public static int[] slice(int fullSize, int slices)
    {
        if (slices > fullSize)
            slices = fullSize;

        int[] ret = new int[slices];

        // сначала добавляем равные части
        int equalPattern = fullSize / slices;
        Arrays.fill(ret, equalPattern);

        fullSize -= equalPattern * slices;

        // потом добавляем остатки
        int i = 0;
        while (fullSize > 0)
        {
            ret[i] += 1;
            fullSize -= 1;
            i++;
            if (i == slices)
                i = 0;
        }

        return ret;
    }
}
