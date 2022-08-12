package ru.es.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.es.log.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProcessUtils
{
	public static String runProcess(String... cmd) throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(cmd);

		Process process = pb.start();

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		InputStream eis = process.getErrorStream();
		InputStreamReader eisr = new InputStreamReader(eis);
		BufferedReader ebr = new BufferedReader(eisr);
		String line;

		StringBuilder ret = new StringBuilder();

		while ((line = br.readLine()) != null)
		{
			ret.append(line);
		}

		while ((line = ebr.readLine()) != null)
		{
			ret.append(line);
		}

		process.destroy();
		is.close();
		isr.close();
		br.close();

		return ret.toString();
	}

	public static JsonElement runToJson(String... cmd) throws Exception
	{
		ProcessBuilder pb = new ProcessBuilder(cmd);


		Process process = pb.start();

		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);

		InputStream eis = process.getErrorStream();
		InputStreamReader eisr = new InputStreamReader(eis);
		BufferedReader ebr = new BufferedReader(eisr);
		String line;


		StringBuilder ret = new StringBuilder();

		while ((line = br.readLine()) != null)
		{
			System.out.println(line);
			ret.append(line);
		}


		JsonElement element = JsonParser.parseString(ret.toString());

		while ((line = ebr.readLine()) != null)
		{
			System.out.println(line);
		}

		process.destroy();
		is.close();
		isr.close();
		br.close();

		return element;
	}

	// делит командную строку на части. Не делит то, что в кавычках
	public static Collection<String> splitCmd(String cmd)
	{
		List<String> ret = new ArrayList<>();

		String[] cmdSplitted = cmd.split(" ");
		for (int i = 0; i < cmdSplitted.length; i++)
		{
			String part = cmdSplitted[i];

			boolean addFully = !part.contains("\"");

			if (!addFully)
			{
				if (part.substring(2).contains("\"")) // когда начало и конец это один фрагмент
					addFully = true;
			}

			if (addFully)
			{
				if (!part.isEmpty())
					ret.add(part);
			}
			else
			{
				String partWithSpaces = part+" ";

				while (true)
				{
					i++;
					part = cmdSplitted[i];

					if (part.contains("\""))
						break;
					else
						partWithSpaces += part+" ";
				}
				partWithSpaces += part;

				ret.add(partWithSpaces);
			}
		}

		return ret;
	}
}
