package ru.es.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.es.log.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

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
}
