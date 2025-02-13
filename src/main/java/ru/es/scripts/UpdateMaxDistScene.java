package ru.es.scripts;

import ru.es.log.Log;
import ru.es.math.ESMath;
import ru.es.util.FileUtils;
import ru.es.util.ListUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class UpdateMaxDistScene
{
	public static void main(String[] args) throws IOException
	{
		File folder = new File("C:\\Users\\sanil\\Documents\\Repos\\play-gve-projects\\GveClient\\Assets\\Data\\Editor\\Resources\\Bundles\\Terrain");

		Set<Double> differentValues = new HashSet<>();

		for (File subfolder : folder.listFiles())
		{
			if (!subfolder.isDirectory())
				continue;
			File mapFile = new File(subfolder, subfolder.getName()+".unity");
			if (!mapFile.exists())
				continue;

			// re
			//if (!subfolder.getName().equalsIgnoreCase("20_22"))
			//	continue;


			Log.warning("prorcess "+subfolder.getName());
			byte[] read = FileUtils.getBytes(mapFile);
			String content = new String(read, StandardCharsets.UTF_8);
			String[] lines = content.split("\n");

			StringBuilder ret = new StringBuilder();
			boolean nextLineIsValue = false;
			int errors = 0;
			for (String line : lines)
			{
				try
				{
					if (nextLineIsValue)
					{
						// '      value: ';
						float value = Float.parseFloat(line.substring(13));
						line = "      value: " + value * 2;
						if (value > 200)
							Log.warning("value warning: " + value);
					}

					nextLineIsValue = line.equals("      propertyPath: MaxDistance");

					ret.append(line);
					ret.append("\n");
				}
				catch (Exception e)
				{
					errors++;
					Log.warning("Error line: "+line);
					//e.printStackTrace();
				}
			}


			Log.warning("Errors: "+errors);
			FileUtils.writeFile(mapFile, ret.toString().getBytes(StandardCharsets.UTF_8));
		}

		Log.warning("values: "+ ListUtils.toString(differentValues, ", "));
	}
}
