package ru.es.audio.audioFile;

import ru.es.jfx.application.HasDialogManager;

import java.io.File;

/**
 * Created by saniller on 06.09.2017.
 */
public interface IBufferedAudioFileUser extends HasDialogManager
{
    void clearFileHref();

    public boolean isRecordEnabled();

    public void playSound(File f);

    public void stopMe();
}
