package ru.es.audio.pattern;


public interface SequencerListener
{
    void penChanged(String what);

    void duplicatePattern(boolean byLoop);
}
