package ru.es.scripts;

import ru.es.log.Log;
import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class DisableHitReaction
{
	public static void main(String[] args) throws IOException
	{
		File folder = new File("C:\\Users\\sanil\\Documents\\Repos\\play-gve-projects\\GveClient\\Assets\\Data\\Editor\\Resources\\Bundles\\Paperdoll\\NPC\\");

		for (File ff : folder.listFiles())
		{
			if (ff.getName().endsWith(".meta"))
				continue;

			byte[] read = FileUtils.getBytes(ff);
			String content = new String(read, StandardCharsets.UTF_8);
			content = content.replace("DisableHitReaction: 0", "DisableHitReaction: 1");


			FileUtils.writeFile(ff, content.toString().getBytes(StandardCharsets.UTF_8));
		}

	}
}
