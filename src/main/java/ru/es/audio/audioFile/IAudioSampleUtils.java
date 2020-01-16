package ru.es.audio.audioFile;

import ru.es.jfx.application.DialogManager;
import ru.es.models.FloatBuffer;

import java.io.File;
import java.io.IOException;

public interface IAudioSampleUtils
{
    public long convertBuffersToTime(int samples, float sampleRate);

    public long convertBuffersToTime(int buffersCount, int bufferSize, float sampleRate);

    public void saveToFile(FloatBuffer[] data, float sampleRate, int bitDepth, File file) throws IOException;

    public SampleData loadAudioFile(DialogManager dialogManager, File fileSource);
}
