package ru.es.audio.device;

import ru.es.audio.AudioProcess;
import ru.es.audio.audioFile.IBufferedAudioFileUser;

import java.io.File;
import java.util.List;

public interface ISampler extends AudioProcess, IDevice, IInstrument
{
    void loadFile(File file);

    List<? extends IBufferedAudioFileUser> getZones();

    boolean isReverceOn();
}
