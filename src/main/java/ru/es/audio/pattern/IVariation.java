package ru.es.audio.pattern;

/**
 * Created by saniller on 05.09.2017.
 */
public interface IVariation
{
    default int getTicksCount()
    {
        return 0;
    }

    default boolean isLive()
    {
        return false;
    }
}
