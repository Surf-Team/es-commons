package ru.es.audio.device;

import ru.es.audio.AudioProcess;

public interface IClipPlayer extends AudioProcess
{
    Clip getRecordingClip();

    void hardStop();

    void arrangeSoundJumped(long currentTick);

    void nextTick(int arrangeTick, long tickSamplePos, boolean soundStarted);

    public void fadesChanged(Clip audioClip);
}
