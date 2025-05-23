package ru.es.process;

import javolution.util.FastTable;
import ru.es.lang.ESSetter;
import ru.es.log.Log;
import ru.es.thread.RunnableImpl;
import ru.es.thread.SingletonThreadPool;
import ru.es.util.Environment;
import ru.es.util.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ProcessFactory
{
	private int maxProcessId = 1;
	private Map<Integer, ProcessInfo> processesById = new ConcurrentHashMap<>();
	private List<ProcessInfo> processes = new FastTable<>();

	public ProcessFactory()
	{
		SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl() {
			@Override
			public void runImpl() throws Exception
			{
				var oldProcesses = processes.stream().filter(f->
				{
					if (f.importantPrio >= 0 || !f.done || f.endTime <= 0)
						return false;

					if (f.endTime + 60*60*1000L < System.currentTimeMillis())
						return true;

					return false;
				}).collect(Collectors.toList());

				processes.removeAll(oldProcesses);
			}
		}, 60000, 60000);
	}

	// multithread
	public void createProcess(File directory, ProcessInfo processInfo, ESSetter<ProcessInfo> onCompleted, String... cmd)
	{
		processInfo.fullCommand = StringUtils.arrayToString(cmd, " ");

		if (processInfo.debug)
			Log.warning("processInfo.fullCommand: "+processInfo.fullCommand);

		processInfo.setId(maxProcessId++);

		Thread thread = new Thread(new RunnableImpl() {
			@Override
			public void runImpl() throws Exception
			{
				try
				{
					runProcess(directory, processInfo, cmd);

					processInfo.done = true;
					//Log.warning("Process thread done.");
					onCompleted.set(processInfo);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					processInfo.error = true;
					processInfo.done = true;
					processInfo.endTime = System.currentTimeMillis();
					onCompleted.set(processInfo);
				}
			}
		});
		thread.start();

		processesById.put(processInfo.getId(), processInfo);
		processes.add(processInfo);
	}

	// one thread
	public void createProcess(File directory, ProcessInfo processInfo, String... cmd)
	{
		processInfo.fullCommand = StringUtils.arrayToString(cmd, " ");
		processInfo.setId(maxProcessId++);


		if (processInfo.debug)
			Log.warning("processInfo.fullCommand: "+processInfo.fullCommand);

		processesById.put(processInfo.getId(), processInfo);
		processes.add(processInfo);

		try
		{
			runProcess(directory, processInfo, cmd);
			processInfo.done = true;
			//Log.warning("Process thread done.");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			processInfo.error = true;
			processInfo.done = true;
			processInfo.endTime = System.currentTimeMillis();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			processInfo.error = true;
			processInfo.done = true;
			processInfo.endTime = System.currentTimeMillis();
		}
	}

	public void addDummyProcess(ProcessInfo processInfo)
	{
		processInfo.setId(maxProcessId++);

		processesById.put(processInfo.getId(), processInfo);
		processes.add(processInfo);
	}

	public static void runProcess(File directory, ProcessInfo processInfo, String... cmd) throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(cmd);

		processInfo.startTime = System.currentTimeMillis();

		if (directory != null)
			pb.directory(directory);


		if (!processInfo.addToPathEnv.isEmpty())
		{
			String pathVar = System.getenv().get("Path");
			Log.warning("init path: " + pathVar);

			boolean isWindows = Environment.isWindows();
			for (String path : processInfo.addToPathEnv)
			{
				if (isWindows)
					pathVar += ";" + path;
				else
					pathVar += ":" + path;
			}
			Log.warning("Updated Path: " + pathVar);
			try
			{
				pb.environment().put("PATH", pathVar);
			}
			catch (Exception e)
			{
				Log.warning("path dir is not updated: " + e.getMessage());
			}
		}

		Process process = pb.start();


		Charset charset = processInfo.getCharset();

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, charset);
		BufferedReader br = new BufferedReader(isr);

		InputStream eis = process.getErrorStream();
		InputStreamReader eisr = new InputStreamReader(eis,  charset);
		BufferedReader ebr = new BufferedReader(eisr);

		exec(process, processInfo, br, ebr);
		checkHasError(processInfo);


		if (process.exitValue() != 0)
		{
			processInfo.error = true;
			Log.warning("Error exit code: "+process.exitValue());
		}

		processInfo.endTime = System.currentTimeMillis();
		process.destroy();
		is.close();
		isr.close();
		br.close();
	}

	private static void checkHasError(ProcessInfo processInfo)
	{
		if (!processInfo.getErrOut().isEmpty())
		{
			boolean error = true;
			for (String successText : processInfo.alwaysSuccessTextInErrorOut)
			{
				if (processInfo.getErrOut().contains(successText))
				{
					error = false;
					break;
				}
			}

			if (error)
			{
				boolean hasErrorLines = true;
				for (String errorLineStr : processInfo.getErrOut().split("\r"))
				{
					for (String notErrorContains : processInfo.isNotErrorLines)
					{
						if (errorLineStr.contains(notErrorContains))
						{
							hasErrorLines = false;
							break;
						}
					}
					if (hasErrorLines)
					{
						break;
					}
				}
				error = hasErrorLines;
			}

			processInfo.error = error;
		}
	}

	private static void exec(Process process, ProcessInfo processInfo, BufferedReader br, BufferedReader ebr) throws IOException, InterruptedException
	{
		String line;

		while (process.isAlive())
		{
			while (br.ready() && (line = br.readLine()) != null)
			{
				processInfo.appendStdout(line);
			}

			while (ebr.ready() && (line = ebr.readLine()) != null)
			{
				processInfo.appendErrout(line);
			}
			Thread.sleep(10);
		}

		Thread.sleep(300);
		while (br.ready() && (line = br.readLine()) != null)
		{
			processInfo.appendStdout(line);
		}

		while (ebr.ready() && (line = ebr.readLine()) != null)
		{
			processInfo.appendErrout(line);
		}
	}

	public Iterable<ProcessInfo> getProcesses()
	{
		return processes;
	}

	public void startProcessLogger()
	{
		Thread t = new Thread(new Runnable() {
			@Override
			public void run()
			{
				while (true)
				{
					for (ProcessInfo processInfo : getProcesses())
					{
						if (processInfo == null)
							continue;
						
						if (processInfo.debug)
						{
							while (!processInfo.stdOutQueue.isEmpty())
							{
								System.out.print(processInfo.stdOutQueue.poll());
							}

							while (!processInfo.stdOutErrQueue.isEmpty())
							{
								System.out.print(processInfo.stdOutErrQueue.poll());
							}
						}
					}
					try
					{
						Thread.sleep(500);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
						break;
					}
				}
			}
		});
		t.setDaemon(true);
		t.setName("logger");
		t.start();
	}

	public ProcessInfo getProcess(int processId)
	{
		for (ProcessInfo processInfo : processes)
		{
			if (processInfo == null)
				continue;
			
			if (processInfo.getId() == processId)
				return processInfo;
		}
		return null;
	}
}
