package ru.es.audio;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.util.ListUtils;
import javafx.collections.ObservableList;

public enum AudioClock
{
    clock_1_64(1,64),

    clock_1_32(1,32),
    clock_1_24(1,24),
    clock_3_64(3,64),

    clock_1_16(1,16),
    clock_1_12(1,12),
    clock_3_32(3,32),
    
    clock_1_8(1,8),
    clock_1_6(1,6),
    clock_3_16(3,16),

    clock_1_4(1,4),
    clock_1_3(1,3),
    clock_3_8(3,8),

    clock_1_2(1,2),
    clock_2_3(2,3),
    clock_3_4(3,4),
    
    clock_1_1(1,1),
    clock_2_1(2,1),
    clock_4_1(4,1),
    clock_8_1(8,1),
    clock_16_1(16,1);

    public final int parts;
    public final int delim;
    public final String name;
    public final int ticks;

    AudioClock(int parts, int delim)
    {
        this.parts = parts;
        this.delim = delim;
        if (parts == 0 && delim == 0)
            this.name = "Off";
        else
            this.name = parts+"/"+delim;

        this.ticks = (12*4*4) * parts / delim;
    }

    AudioClock(int parts, int delim, String name)
    {
        this.parts = parts;
        this.delim = delim;
        this.name = name;

        this.ticks = (12*4*4) * parts / delim;
    }

    @Override
    public String toString()
    {
        return name;
    }

    public static ObservableList<AudioClock> delayClocks = ListUtils.createObservableList(clock_1_32, clock_1_24, clock_3_64, clock_1_16, clock_1_12, clock_3_32,
            clock_1_8, clock_1_6, clock_3_16, clock_1_4, clock_1_3, clock_3_8);

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static double convertToRate(AudioClock audioClock, double bpm)
    {
        // bpm -> beatsPerSeconds -> barsPerSeconds
        double barsPerSecond = bpm / 60.0 * 4;
        
        double partOfBar = (double) audioClock.delim / audioClock.parts / 16.0;

        return barsPerSecond * partOfBar;
    }
}
