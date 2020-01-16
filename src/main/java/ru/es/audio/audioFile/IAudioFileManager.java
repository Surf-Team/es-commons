package ru.es.audio.audioFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface IAudioFileManager
{
    public Map<BufferedAudioFile, List<IBufferedAudioFileUser>> getLostAudioFileUsers();

    public Map<BufferedAudioFile, File> getLostFiles();
}
