package ru.es.audio;

/**
 * Created by saniller on 22.04.2017.
 */
public interface ESAudioChannel
{
    void write(float[] output);

    void read(float[] input);

    boolean isInput();

    String getChannelName();

    boolean allowAddToFloatsHolder();

}
