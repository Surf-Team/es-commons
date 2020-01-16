package ru.es.audio.audioFile;

import ru.es.models.FloatBuffer;

import javax.sound.sampled.AudioFormat;

public class SampleData
{
    public FloatBuffer[] data2;
    public Float sampleRate;
    public AudioFormat audioFormat; // format on load

    public SampleData(FloatBuffer[] data2, Float sampleRate, AudioFormat audioFormat)
    {
        this.data2 = data2;
        this.sampleRate = sampleRate;
        this.audioFormat = audioFormat;
    }
}
