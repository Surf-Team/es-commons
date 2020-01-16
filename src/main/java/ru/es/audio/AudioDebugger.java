package ru.es.audio;

import ru.es.log.Log;

public class AudioDebugger
{
    public static AudioDebugger globalDebugger100 = new AudioDebugger("Global", 100, false);
    public static AudioDebugger globalDebugger1000 = new AudioDebugger("Global", 1000, false);
    public static AudioDebugger globalDebugger10000 = new AudioDebugger("Global", 10000, false);

    public long count = 0;
    public long timeFull = 0;
    public long timeMin = 0;
    public long timeMax = 0;
    public int enterFails = 0;
    long noteProcessTime;

    String name;
    int averageCount;
    boolean checkOneInOneOut = false;
    boolean entered = false;

    public AudioDebugger(String name, int averageCount, boolean checkOneInOneOut)
    {
        this.name = name;
        this.averageCount = averageCount;
        this.checkOneInOneOut = checkOneInOneOut;
    }

    public void start()
    {
        if (!checkOneInOneOut)
            noteProcessTime = System.nanoTime();
        else
        {
            if (!entered)
            {
                noteProcessTime = System.nanoTime();
                entered = true;
            }
            else
            {
                enterFails++;
            }
        }
    }

    public void finish()
    {
        count++;
        noteProcessTime = (System.nanoTime()-noteProcessTime)/1000;
        timeFull += noteProcessTime;
        entered = false;

        if (noteProcessTime > timeMax)
            timeMax = noteProcessTime;
        if (noteProcessTime < timeMin)
            timeMin = noteProcessTime;

        if (count > averageCount)
        {
            if (!checkOneInOneOut)
                Log.warning(name+": micros: "+(timeFull /averageCount)+", min: "+ timeMin +", max: "+ timeMax);
            else
                Log.warning(name+": micros: "+(timeFull /averageCount)+", min: "+ timeMin +", max: "+ timeMax+", enterFails: "+enterFails);


            count = 0;
            timeFull = 0;
            timeMax = 0;
            timeMin = 0;
            enterFails = 0;
        }
    }
}
