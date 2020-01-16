package ru.es.audio.driver;

import java.util.List;

public interface IAudioDriverManager
{
    AudioDriver getDriver(String driverName);

    public List<String> getDriverNames();
}
