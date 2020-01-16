package ru.es.audio.device;

public interface IDevice
{
    public void stopAllNotes();

    public void softStopAllNotes(long position);
}
