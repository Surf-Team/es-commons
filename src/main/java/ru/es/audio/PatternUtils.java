package ru.es.audio;

public class PatternUtils
{
    public static final int TICKS_IN_STEP = 12;
    public static final int BEAT = TICKS_IN_STEP*4;
    public static final int MAX_LINES_COUNT = 64;
    public static final int STEPS_IN_BAR = 4*4;
    public static final int TICKS_IN_BAR = STEPS_IN_BAR * TICKS_IN_STEP;
}
