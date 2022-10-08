package ru.es.models;

import ru.es.lang.ESEventHandler;
import ru.es.log.Log;
import ru.es.util.Environment;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessInfo
{
	public final String name;
	public List<String> isNotErrorLines = new ArrayList<>();
	public List<String> alwaysSuccessTextInErrorOut = new ArrayList<>();
	private String stdOut = "";
	private String errOut = "";
	public boolean done = false;
	public boolean error = false;
	private int id;
	public int stepIndex;
	public String functionName;
	public Charset charset;
	public String group;
	public boolean logAddProcessName = true;

	public boolean debug;

	public String fullCommand;

	public String stage;
	public int projectId;
	public String projectName;

	public ConcurrentLinkedQueue<String> stdOutQueue = new ConcurrentLinkedQueue<>();
	public ConcurrentLinkedQueue<String> stdOutErrQueue = new ConcurrentLinkedQueue<>();

	public ESEventHandler<String> stdOutAdded = new ESEventHandler<>();
	public ESEventHandler<String> stdErrAdded = new ESEventHandler<>();

	public ProcessInfo(String name, int stepIndex, String functionName)
	{
		this.name = name;
		this.stepIndex = stepIndex;
		this.functionName = functionName;

		charset = Charset.defaultCharset();
		if (Environment.isWindows())
			charset = Charset.forName("IBM866");
	}

	public synchronized void appendStdout(String data)
	{
		stdOut += data;
		stdOut += System.lineSeparator();
		stdOutQueue.offer(data);
		stdOutQueue.offer(System.lineSeparator());

		stdOutAdded.event(data);

		if (debug)
		{
			if (logAddProcessName)
				Log.warning("STD: " + name + ": " + data);
			else
				System.out.println(data);
		}
	}

	public synchronized void appendErrout(String data)
	{
		errOut += data;
		errOut += System.lineSeparator();
		stdOutErrQueue.offer(data);
		stdOutErrQueue.offer(System.lineSeparator());

		stdErrAdded.event(data);

		if (debug)
		{
			if (logAddProcessName)
				Log.warning("ERR: " + name + ": " + data);
			else
				System.out.println(data);
		}
	}

	public String getStdout()
	{
		return stdOut;
	}

	public String getErrOut()
	{
		return errOut;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public Charset getCharset()
	{
		return charset;
	}
}
