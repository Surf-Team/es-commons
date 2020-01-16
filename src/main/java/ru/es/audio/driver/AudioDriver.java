package ru.es.audio.driver;

import ru.es.audio.ESAudioChannel;

import java.util.Set;

public interface AudioDriver
{
    String getName();

    int getNumChannelsInput();

    int getNumChannelsOutput();

    boolean canSampleRate(double sampleRate);

    void setSampleRate(double rate);

    double getSampleRate();

    void addDriverListener(AudioDriverListener listener);

    int getBufferMaxSize();

    default int getBufferPreferredSize()
    {
        return 1024;
    }

    void start(Set<ESAudioChannel> channelsToInit, int newBufferSize);

    void shutdownAndUnloadDriver();

    ESAudioChannel getChannelInput(int i);

    ESAudioChannel getChannelOutput(int i);

    boolean isRunning();

    void requestShutdown();

    void reloadChannels();

    Type getType();

    void showControlPanel();

    public enum Type
    {
        PortAudio,
        Asio
    }
}
