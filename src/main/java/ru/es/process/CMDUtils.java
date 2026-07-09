package ru.es.process;

import ru.es.log.Log;
import ru.es.util.Environment;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class CMDUtils
{
	public static void removeDirectory(ProcessFactory processFactory, String functionDesc, File file) throws Exception
	{
		if (!file.exists())
			return;
		
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 1, "CMDUtils.removeDirectory");
		processInfo.importantPrio = -1;
		if (Environment.isWindows())
		{
			processInfo.charset = Charset.forName("windows-1251");
			processFactory.createProcess(new File("./"), processInfo, "CMD", "/C", "rmdir", "/s", "/q", file.getPath());
		}
		else
		{
			processInfo.charset = StandardCharsets.UTF_8;
			processFactory.createProcess(new File("./"), processInfo, "rm", "-rf", file.getPath());
		}
		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Cant delete tmp directory!");
		}
	}

	public static void addToArchive(ProcessFactory processFactory, String functionDesc,
									File launchDirectory, File archiveFile, File directoryToArchivate) throws Exception
	{
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.addToArchive");
		processInfo.addToPathEnv.add(new File("./7-Zip/").getAbsoluteFile().getAbsolutePath());

		if (Environment.isWindows())
			processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "a", "-tzip", archiveFile.getPath(), directoryToArchivate.getAbsolutePath());
		else
			processFactory.createProcess(launchDirectory, processInfo, "zip", archiveFile.getPath(), directoryToArchivate.getAbsolutePath());


		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Ошибка создания архива\r\n"+processInfo.getErrOut());
		}
		if (!archiveFile.exists())
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Архив не был создан\r\n"+processInfo.getErrOut());
		}
	}

	public static void addToArchiveV2(ProcessFactory processFactory, String functionDesc,
									File launchDirectory, String archiveFile, String directoryToArchivate) throws Exception
	{
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.addToArchive");

		if (Environment.isWindows())
		{
			processInfo.addToPathEnv.add(new File("./7-Zip/").getAbsoluteFile().getAbsolutePath());
			processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "a", "-tzip", archiveFile, directoryToArchivate);
		}
		else
			processFactory.createProcess(launchDirectory, processInfo, "zip", "-r", archiveFile, directoryToArchivate);


		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Ошибка создания архива\r\n"+processInfo.getErrOut());
		}
	}

	public static void extractFromArchive(ProcessFactory processFactory, String functionDesc,
									File launchDirectory, File archiveFile) throws Exception
	{
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.extractFromArchive");
		processInfo.addToPathEnv.add(new File("./7-Zip/").getAbsoluteFile().getAbsolutePath());

		if (Environment.isWindows())
			processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "x", archiveFile.getAbsolutePath());
		else
			processFactory.createProcess(launchDirectory, processInfo, "unzip", archiveFile.getAbsolutePath());
			
		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Архив не был распакован\r\n" + processInfo.getErrOut());
		}
	}

	public static void removeFile(ProcessFactory processFactory, String functionDesc, File archiveFile) throws Exception
	{
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.removeFile");

		if (Environment.isWindows())
		{
			processInfo.charset = Charset.forName("windows-1251");
			processFactory.createProcess(new File("./"), processInfo, "CMD", "/C", "del", "/f", archiveFile.getAbsolutePath());
		}
		else
		{
			processInfo.charset = StandardCharsets.UTF_8;
			processFactory.createProcess(new File("./"), processInfo, "rm", archiveFile.getAbsolutePath());
		}
		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Файл не был удалён: "+archiveFile.getAbsolutePath()+"\r\n" + processInfo.getErrOut());
		}
	}

	public static ProcessInfo copyFolder(ProcessFactory processFactory, String functionDesc, File src, File dest) throws Exception
	{
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.copyFolder");
		processInfo.debug = false;
		//processInfo.charset = Charset.forName("windows-1251");

		if (Environment.isWindows())
		{
			processFactory.createProcess(new File("./"), processInfo, "CMD", "/C",
					"xcopy", src.getAbsolutePath() + "\\", dest.getAbsolutePath() + "\\", "/e", "/y");
		}
		else
		{
			processFactory.createProcess(new File("./"), processInfo, "cp", "-r",
					src.getAbsolutePath() + "/", dest.getParentFile().getAbsolutePath() + "/");
		}
		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Директория не была скопирована: "+src.getAbsolutePath()+"\r\n" + processInfo.getErrOut());
		}
		return processInfo;
	}


}
