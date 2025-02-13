package ru.es.scripts;

import ru.es.log.Log;
import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;

public class CopyFiles
{
	public static void main(String[] args) throws IOException
	{
		Log.warning("Arg 0 is config file");
		Log.warning("Arg 1 is src file");
		Log.warning("Arg 2 is dest postfix target file");
		File configFile = new File(args[0]);
		File srcFile = new File(args[1]);

		if (!configFile.exists())
			throw new RuntimeException("Config File "+args[0]+" doesnt exist!");

		if (!srcFile.exists())
			throw new RuntimeException("Src File "+args[1]+" doesnt exist!");

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

		for (String s : lines)
		{
			File dest = new File(s, args[2]);
			Log.warning("Begin copy from "+srcFile.getAbsolutePath()+" to "+dest.getAbsolutePath());
			FileUtils.copyFile(srcFile, dest);
		}
		Log.warning("CopyFile script done.");
	}
}
