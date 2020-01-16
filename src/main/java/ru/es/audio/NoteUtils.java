package ru.es.audio;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by saniller on 03.10.2016.
 */
public class NoteUtils
{
    public static final int octaveSize = 12; // нот в октаве всегда 12.


    static String[][] noteWords =
            {
                    { "C", "C#", "D", "D#","E","F","F#","G","G#","A","A#","B"},
                    { "C", "Db", "D", "Eb","E","F","Gb","G","Ab","A","Bb","B"},
            };

    private static String getNoteName(int note, boolean octave, int type, boolean octaveFirst)
    {
        if (note < 0 || note > 128)
            return "incorrect";

        String octaveWord = "" + ((note / 12));
        if (!octave)
            octaveWord = "";

        String noteWord = noteWords[type][note % 12];

        if (octaveFirst)
            return octaveWord + noteWord;
        else
            return noteWord+octaveWord;
    }


    // название ноты в английской системе, например С0 - нота до октавы 0, A5# - нота ля диез октавы 5
    public static String[] noteNames;
    public static Map<String, Integer> notesNames2 = new HashMap<>();
    static
    {
        noteNames = new String[128];
        try
        {
            for (int i = 0; i < 128; i++)
            {
                noteNames[i] = getNoteName(i, true, 0, true);
            }
            for (int i = -3*12; i < 128; i++)
            {
                notesNames2.put(getNoteName(i, true, 0, true), i);
                notesNames2.put(getNoteName(i, true, 1, true), i);
                notesNames2.put(getNoteName(i, true, 0, false), i);
                notesNames2.put(getNoteName(i, true, 1, false), i);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
    }


    // принадлежит ли нота чёрной клавише на пианино
    static int[] blackNotesInOctave = new int[] { 1, 3, 6, 8, 10 };
    public static boolean isBlack(int note)
    {
        int noteInOctave = note % octaveSize;
        for (int i : blackNotesInOctave)
        {
            if (noteInOctave == i)
                return true;
        }
        return false;
    }

    public static String getNoteName(int note)
    {
        if (note < 0 || note > 128)
            return "incorrect";

        return noteNames[note];
    }

    public static int getNote(String noteName)
    {
        for (String s : notesNames2.keySet())
        {
            if (s.equalsIgnoreCase(noteName))
                return notesNames2.get(s);
        }

        return 60;
    }
}
