package ru.es.lang.interval;

import ru.es.jfx.binding.ESProperty;
import ru.es.lang.ESEventDispatcher;

public class IntervalBase implements Interval
{
    public final ESProperty<Number> start = new ESProperty<>();
    public final ESProperty<Number> end = new ESProperty<>();
    public ESEventDispatcher somethingChanged = new ESEventDispatcher(start, end);

    public IntervalBase()
    {

    }

    public IntervalBase(Number start, Number end)
    {
        this.start.set(start);
        this.end.set(end);
    }

    @Override
    public Number getStart()
    {
        return start.get();
    }

    @Override
    public Number getEnd()
    {
        return end.get();
    }

    @Override
    public void setStart(Number n)
    {
        start.set(n);
    }

    @Override
    public void setEnd(Number n)
    {
        end.set(n);
    }

    public void setAll(Number n)
    {
        setStart(n);
        setEnd(n);
    }
}
