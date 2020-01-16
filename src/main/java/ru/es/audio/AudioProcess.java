package ru.es.audio;

/**
 * Created by saniller on 15.06.2016.
 */
public interface AudioProcess
{
    float[][] getNextFloats(float[][] inputs, AudioStamp info);
}
