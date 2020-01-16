package ru.es.lang.interval;

public interface Interval
{
    public Number getStart();
    public Number getEnd();
    public void setStart(Number n);
    public void setEnd(Number n);

    default int getStartInt()
    {
        if (getStart() != null)
            return getStart().intValue();
        else
            return 0;
    }

    default int getEndInt()
    {
        if (getEnd() != null)
            return getEnd().intValue();
        else
            return 0;
    }
}
