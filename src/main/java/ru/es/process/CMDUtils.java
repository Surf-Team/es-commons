package ru.es.process;

import ru.es.log.Log;
import ru.es.util.Environment;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
		if (Environment.isWindows())
		{
			ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.addToArchive");
			processInfo.addToPathEnv.add(new File("./7-Zip/").getAbsoluteFile().getAbsolutePath());
			processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "a", "-tzip", archiveFile.getPath(), directoryToArchivate.getAbsolutePath());
			if (processInfo.error)
			{
				Log.warning(processInfo.getStdout());
				Log.warning(processInfo.getErrOut());
				throw new Exception("Ошибка создания архива\r\n" + processInfo.getErrOut());
			}
		}
		else
		{
			zipDirectoryToFile(directoryToArchivate, archiveFile);
		}

		if (!archiveFile.exists())
			throw new Exception("Архив не был создан");
	}

	public static void addToArchiveV2(ProcessFactory processFactory, String functionDesc,
									File launchDirectory, String archiveFile, String directoryToArchivate) throws Exception
	{
		if (Environment.isWindows())
		{
			ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.addToArchive");
			processInfo.addToPathEnv.add(new File("./7-Zip/").getAbsoluteFile().getAbsolutePath());
			processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "a", "-tzip", archiveFile, directoryToArchivate);
			if (processInfo.error)
			{
				Log.warning(processInfo.getStdout());
				Log.warning(processInfo.getErrOut());
				throw new Exception("Ошибка создания архива\r\n" + processInfo.getErrOut());
			}
		}
		else
		{
			zipDirectoryToFile(new File(directoryToArchivate), new File(archiveFile));
		}
	}

	public static void extractFromArchive(ProcessFactory processFactory, String functionDesc,
									File launchDirectory, File archiveFile) throws Exception
	{
		if (Environment.isWindows())
		{
			ProcessInfo processInfo = new ProcessInfo(functionDesc, 2, "CMDUtils.extractFromArchive");
			processInfo.addToPathEnv.add(new File("./7-Zip/").getAbsoluteFile().getAbsolutePath());
			processFactory.createProcess(launchDirectory, processInfo, "CMD", "/C", "7z.exe", "x", archiveFile.getAbsolutePath());
			if (processInfo.error)
			{
				Log.warning(processInfo.getStdout());
				Log.warning(processInfo.getErrOut());
				throw new Exception("Архив не был распакован\r\n" + processInfo.getErrOut());
			}
		}
		else
		{
			unzipFile(archiveFile, archiveFile.getParentFile());
		}
	}

	private static void zipDirectoryToFile(File sourceDir, File destZip) throws IOException
	{
		try (FileOutputStream fos = new FileOutputStream(destZip);
			 ZipOutputStream zos = new ZipOutputStream(fos))
		{
			zipEntry(sourceDir, sourceDir.getParentFile(), zos);
		}
	}

	private static void zipEntry(File source, File baseDir, ZipOutputStream zos) throws IOException
	{
		File[] files = source.listFiles();
		if (files == null)
			return;
		for (File file : files)
		{
			String entryName = baseDir.toURI().relativize(file.toURI()).getPath();
			if (file.isDirectory())
			{
				zos.putNextEntry(new ZipEntry(entryName.endsWith("/") ? entryName : entryName + "/"));
				zos.closeEntry();
				zipEntry(file, baseDir, zos);
			}
			else
			{
				zos.putNextEntry(new ZipEntry(entryName));
				Files.copy(file.toPath(), zos);
				zos.closeEntry();
			}
		}
	}

	private static void unzipFile(File zipFile, File destDir) throws IOException
	{
		byte[] buffer = new byte[8192];
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile)))
		{
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null)
			{
				File outFile = new File(destDir, entry.getName());
				if (entry.isDirectory())
				{
					outFile.mkdirs();
				}
				else
				{
					outFile.getParentFile().mkdirs();
					try (FileOutputStream fos = new FileOutputStream(outFile))
					{
						int len;
						while ((len = zis.read(buffer)) > 0)
							fos.write(buffer, 0, len);
					}
				}
				zis.closeEntry();
			}
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
