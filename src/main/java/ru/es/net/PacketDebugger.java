package ru.es.net;


public interface PacketDebugger
{
    void onPacketSend(Object gsp);
    void onPacketReceived(Object msg);
}
