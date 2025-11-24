package ru.es.process;

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
	public String vm;
	private String stdOut = "";
	private String errOut = "";
	public boolean done = false;
	public boolean error = false;
	public int importantPrio = 1;
	private int id;
	public int stepIndex;
	public String functionName;
	public Charset charset;
	public String group;
	public boolean logAddProcessName = true;
	public long startTime = 0;
	public long endTime = 0;

	public List<String> addToPathEnv = new ArrayList<>();

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
		stdOut += "\r\n";
		stdOutQueue.offer(data);
		stdOutQueue.offer("\r\n");

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
		errOut += "\r\n";
		stdOutErrQueue.offer(data);
		stdOutErrQueue.offer("\r\n");

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
