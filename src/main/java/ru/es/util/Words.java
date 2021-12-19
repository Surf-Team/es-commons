package ru.es.util;


import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 15.09.14
 * Time: 0:30
 * To change this template use File | Settings | File Templates.
 */
public class Words
{
    public static final String[] words = { "A", "B", "C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
    public static final String[] noteWords = { "C", "C#", "D", "D#","E","F","F#","G","G#","A","A#","B"};
    public static final String[] wordsAll = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    public static String getWord(int index)
    {
        if (index == -1)
            return "unk";
        if (index < words.length)
            return words[index];
        else
        {
            int secondIndex = index / words.length;
            return words[(secondIndex - 1) % words.length] + words[index % words.length];
        }
    }

    public static String getNotesFromList(Collection<Integer> notes)
    {
        String ret = "";
        for (Integer i : notes)
        {
            ret += getNoteName(i, true) + " ";
        }
        ret = ret.substring(0, ret.length()-1);
        return ret;
    }

    public static String getNoteName(int note, boolean octave)
    {
        if (note < 0 || note > 128)
            return "";

        String firstWord = "" + ((note / 12));
        if (!octave)
            firstWord = "";
        String secondWord = noteWords[note % 12];
        return firstWord + secondWord;
    }

    public static boolean isNumber(String word)
    {
        if (word.equals("1") || word.equals("2") || word.equals("3") || word.equals("4") || word.equals("5") ||
                word.equals("6") || word.equals("7") || word.equals("8") || word.equals("9") || word.equals("0"))
            return true;

        return false;
    }

    public static String removeNumbersAtTheEnd(String word)
    {
        int numsAdTheEnd = 0;
        for (int i = word.length()-1; i >= 0; i--)
        {
            String currentWord = word.substring(i, i+1);
            if (Words.isNumber(currentWord))
                numsAdTheEnd++;
        }
        String clearName = word.substring(0, word.length()-numsAdTheEnd);
        return clearName;
    }

    public static int getNumberAtTheEnd(String word)
    {
        int numsAdTheEnd = 0;
        for (int i = word.length()-1; i >= 0; i--)
        {
            String currentWord = word.substring(i, i+1);
            if (Words.isNumber(currentWord))
                numsAdTheEnd++;
        }
        String numName = word.substring(word.length()-numsAdTheEnd, word.length());
        int ret = 0;
        if (numName.length() > 0)
            ret = Integer.parseInt(numName);

        return ret;
    }

    public static String getStepName(int step)
    {
        int beat = step % 16;
        int bar = step / 16;
        return  bar+"."+beat;
    }


}
