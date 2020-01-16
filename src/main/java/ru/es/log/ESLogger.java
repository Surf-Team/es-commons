package ru.es.log;

public interface ESLogger
{
    public void warning(String text);

    public void info(String text);

    public void error(String text);

    public void hardGui(String text);

    public void debug(String text);

    public void event(String text);
}
