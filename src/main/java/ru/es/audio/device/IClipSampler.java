package ru.es.audio.device;

import ru.es.audio.AudioStamp;
import ru.es.audio.audioFile.BufferedAudioFile;

public interface IClipSampler extends ISampler
{
    void fadesChanged();

    public BufferedAudioFile getBufferedAudioFile();

    void showControlPanel();

    public double getPitchOffset();

    void stopRecord();

    void startRecord(AudioStamp stamp);
}
