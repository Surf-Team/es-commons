package ru.es.util;

import javolution.util.FastTable;
import ru.es.lang.ESSetter;
import ru.es.log.Log;
import ru.es.models.ProcessInfo;
import ru.es.thread.RunnableImpl;
import ru.es.thread.SingletonThreadPool;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProcessFactory
{
	private int maxProcessId = 0;
	private Map<Integer, ProcessInfo> processesById = new ConcurrentHashMap<>();
	private List<ProcessInfo> processes = new FastTable<>();

	public ProcessFactory()
	{

	}

	// multithread
	public ProcessInfo createProcess(File directory, String name, int project, String stage, ESSetter<ProcessInfo> onCompleted, String... cmd)
	{
		ProcessInfo processInfo = new ProcessInfo(name, project, stage);
		processInfo.id = maxProcessId++;
		//todo set id
		//toso save to db
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
					onCompleted.set(processInfo);
				}
			}
		});
		thread.start();

		processesById.put(processInfo.id, processInfo);
		processes.add(processInfo);

		return processInfo;
	}

	// one thread
	public void createProcess(File directory, ProcessInfo processInfo, String... cmd)
	{
		processInfo.id = maxProcessId++;

		processesById.put(processInfo.id, processInfo);
		processes.add(processInfo);

		try
		{
			runProcess(directory, processInfo, cmd);
			processInfo.done = true;
			//Log.warning("Process thread done.");
		}
		catch (IOException e)
		{
			processInfo.error = true;
			processInfo.done = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			processInfo.error = true;
			processInfo.done = true;
		}
	}

	public static void runProcess(File directory, ProcessInfo processInfo, String... cmd) throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(cmd);

		if (directory != null)
			pb.directory(directory);

		Process process = pb.start();

		Charset charset = Charset.defaultCharset();
		if (Environment.isWindows())
			charset = Charset.forName("IBM866");

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, charset);
		BufferedReader br = new BufferedReader(isr);

		InputStream eis = process.getErrorStream();
		InputStreamReader eisr = new InputStreamReader(eis,  charset);
		BufferedReader ebr = new BufferedReader(eisr);
		String line;

		while ((line = br.readLine()) != null)
		{
			processInfo.appendStdout(line);
		}

		while ((line = ebr.readLine()) != null)
		{
			processInfo.appendErrout(line);
		}



		if (!processInfo.getErrOut().isEmpty())
			processInfo.error = true;

		if (process.exitValue() != 0)
		{
			processInfo.error = true;
			Log.warning("Error exit code: "+process.exitValue());
		}

		process.destroy();
		is.close();
		isr.close();
		br.close();
	}

	public Iterable<ProcessInfo> getProcesses()
	{
		return processes;
	}
}
