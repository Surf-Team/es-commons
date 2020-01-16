package ru.es.audio.pattern;

public interface INoteReceiver
{
    public void noteReceived(Tone tone, boolean start, int velo, boolean updatePen, boolean addToAccordHistory, long position);
}
