package ru.es.log;

public interface ESLogger
{
    void warning(String text);

    void info(String text);

    void error(String text);

    void hardGui(String text);

    void debug(String text);

    void event(String text);

    default void fine(String s)
    {
        // default none
    }
}
