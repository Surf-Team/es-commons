package ru.es.models;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessInfo
{
	public final String name;
	private String stdOut = "";
	private String errOut = "";
	public boolean done = false;
	public boolean error = false;
	public int id;
	public int stepIndex;
	public String functionName;

	public ConcurrentLinkedQueue<String> stdOutQueue = new ConcurrentLinkedQueue<>();
	public ConcurrentLinkedQueue<String> stdOutErrQueue = new ConcurrentLinkedQueue<>();

	public ProcessInfo(String name, int stepIndex, String functionName)
	{
		this.name = name;
		this.stepIndex = stepIndex;
		this.functionName = functionName;
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
