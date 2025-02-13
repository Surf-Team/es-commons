package ru.es.net;

public abstract class IClient
{
	public boolean protocol2 = false; // == isPlayGve
	public String userIP;
	public int protocolVer = 0;
	public volatile boolean wantToClose;
	public int connId;

	public GameClientState state;
	public int localPort;
	public String accountName;

	public PacketDebugger packetDebugger = null;


	public IClient getTransferableClientInfo()
	{
		return null;
	}

	@Deprecated
	public boolean isClassic()
	{
		return false;
	}

	@Deprecated
	public boolean isClassicClient()
	{
		return false;
	}

	public int getCryptType()
	{
		return 0;
	}
}
