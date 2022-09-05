package ru.es.scripts;

import ru.es.log.Log;
import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class CopyTheFiles
{
	// копирование нескольких файлов, описанных в конфиге в одну папку
	public static void main(String[] args) throws IOException
	{
		Log.warning("Arg 0 is config file");
		Log.warning("Arg 1 is src folder");
		Log.warning("Arg 2 is dest folder");
		File configFile = new File(args[0]);
		File srcFolder = new File(args[1]);

		// ?
		if (args[2].endsWith("\""))
			args[2] = args[2].substring(0, args[2].length()-1);

		File destFolder = new File(args[2]);

		Log.warning("Config file: "+configFile.getAbsolutePath());
		Log.warning("Src folder: "+srcFolder.getAbsolutePath());
		Log.warning("Dest folder: "+destFolder.getAbsolutePath());

		if (!configFile.exists())
			throw new RuntimeException("Config File "+args[0]+" doesnt exist!");

		if (!srcFolder.exists())
			throw new RuntimeException("Src Folder "+args[1]+" doesnt exist!");

		if (!srcFolder.isDirectory())
			throw new RuntimeException("Src Folder "+args[1]+" is not directory!");

		if (!destFolder.exists())
			throw new RuntimeException("dest Folder "+args[2]+" doesnt exist!");

		if (!destFolder.isDirectory())
			throw new RuntimeException("dest Folder "+args[2]+" is not directory!");

		String[] lines = null;

		try
		{
			lines = FileUtils.readLines(configFile);
		}
		catch (IOException e)
		{
			Log.warning("Error reading config file");
			throw e;
		}

		for (String sourceFileStr : lines)
		{
			File sourceFile = new File(srcFolder, sourceFileStr);
			File destFile = new File(destFolder, sourceFileStr);
			Log.warning("Begin copy from "+sourceFile.getAbsolutePath()+" to "+destFile.getAbsolutePath());
			FileUtils.copyFile(sourceFile, destFile);
		}
		Log.warning("CopyTheFiles script done.");
	}
}
