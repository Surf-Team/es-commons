package ru.es.util;

import ru.es.log.Log;
import ru.es.models.ProcessInfo;

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
		processInfo.charset = Charset.forName("windows-1251");
		processFactory.createProcess(new File("./"), processInfo, "CMD", "/C", "rmdir", "/s", "/q", file.getPath());
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
		processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "a", "-tzip", archiveFile.getPath(), directoryToArchivate.getAbsolutePath());
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
	public static File addToArchive(ProcessFactory processFactory, String functionDesc,
									File launchDirectory, String archiveFile, String directoryToArchivate) throws Exception
	{
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.addToArchive");
		processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "a", "-tzip", archiveFile, directoryToArchivate);
		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Ошибка создания архива\r\n"+processInfo.getErrOut());
		}

		File archiveFileCreated = new File(launchDirectory, archiveFile);
		if (!archiveFileCreated.exists())
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Архив не был создан\r\n"+processInfo.getErrOut());
		}

		return archiveFileCreated;
	}

	public static void extractFromArchive(ProcessFactory processFactory, String functionDesc,
									File launchDirectory, File archiveFile) throws Exception
	{
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.extractFromArchive");
		processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "x", archiveFile.getAbsolutePath());
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
		processInfo.charset = Charset.forName("windows-1251");
		processFactory.createProcess(new File("./"), processInfo, "CMD", "/C", "del", "/f", archiveFile.getAbsolutePath());
		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Файл не был удалён: "+archiveFile.getAbsolutePath()+"\r\n" + processInfo.getErrOut());
		}
	}

	public static void copyFolder(ProcessFactory processFactory, String functionDesc, File src, File dest) throws Exception
	{
		ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.removeFile");
		processInfo.debug = true;
		//processInfo.charset = Charset.forName("windows-1251");
		processFactory.createProcess(new File("./"), processInfo, "CMD", "/C",
				"xcopy", src.getAbsolutePath()+"\\", dest.getAbsolutePath()+"\\", "/e", "/y");
		if (processInfo.error)
		{
			Log.warning(processInfo.getStdout());
			Log.warning(processInfo.getErrOut());
			throw new Exception("Директория не была скопирована: "+src.getAbsolutePath()+"\r\n" + processInfo.getErrOut());
		}
	}
}
