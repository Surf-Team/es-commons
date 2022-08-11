package ru.es.models;

import ru.es.log.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessInfo
{
	public final String name;
	private String stdOut = "";
	private String errOut = "";
	public boolean done = false;
	public boolean error = false;
	public int id; //todo вот это куда то вынести
	public int project;
	public String stage;

	public ConcurrentLinkedQueue<String> stdOutQueue = new ConcurrentLinkedQueue<>();
	public ConcurrentLinkedQueue<String> stdOutErrQueue = new ConcurrentLinkedQueue<>();

	public ProcessInfo(String name, int project, String stage)
	{
		this.name = name;
		this.project = project;
		this.stage = stage;
	}

	public synchronized void appendStdout(String data)
	{
		stdOut += data;
		stdOut += System.lineSeparator();
		stdOutQueue.offer(data);
		stdOutQueue.offer(System.lineSeparator());
		//Log.warning("STD: "+name+"->"+project+"->"+stage+": "+data);
		// event handler . set (data)
	}

	public synchronized void appendErrout(String data)
	{
		errOut += data;
		errOut += System.lineSeparator();
		stdOutErrQueue.offer(data);
		stdOutErrQueue.offer(System.lineSeparator());
		//Log.warning("ERR: "+name+"->"+project+"->"+stage+": "+data);
		// event handler . set (data)
	}

	public String getStdout()
	{
		return stdOut;
	}

	public String getErrOut()
	{
		return errOut;
	}
}
