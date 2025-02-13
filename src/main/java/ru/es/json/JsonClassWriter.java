package ru.es.json;

import ru.es.util.FileUtils;
import ru.es.util.ListUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class JsonClassWriter
{
	public JsonClassWriter()
	{

	}

	public void write(JsonClassParser jsonParser, File folder) throws IOException
	{

		for (String s : sort(jsonParser.classProperties.keySet()))
		{
			String out = buildString(jsonParser, s);

			String[] splitted = s.split("\\.");
			FileUtils.writeFile(new File(folder, splitted[splitted.length-1]+".txt"), out.toString());
		}
	}

	public String buildString(JsonClassParser jsonParser, String classProp)
	{
		StringBuilder out = new StringBuilder();

		JsonClassInfo classInfo;

		if (jsonParser.classProperties.isEmpty())
			throw new RuntimeException("Classes is empty! Empty array?");
		else
			classInfo = jsonParser.classProperties.get(classProp);

		out.append("["+classProp+"]\r\n");

		// тут только базовые типо названия объектов итд
		printPrimitives(out, classInfo, 1);

		for (String fieldName : sort(classInfo.objects.keySet()))
		{
			processObject(2, "["+fieldName+"]", classInfo.objects.get(fieldName), out);
		}

		for (String fieldName : sort(classInfo.arrays.keySet()))
		{
			processObject(2, "[array] "+fieldName, classInfo.arrays.get(fieldName), out);
		}

		out.append("\r\n\r\n");

		return out.toString();
	}


	private void printPrimitives(StringBuilder out, JsonClassInfo classInfo, int tabCount)
	{
		for (String fieldName : sort(classInfo.primitiveFields))
		{
			for (int i = 0; i < tabCount; i++)
				out.append("- ");
			out.append(fieldName);

			var examples = classInfo.primitiveFieldExamples.get(fieldName);
			if (examples != null)
			{
				out.append("\t\t\tразных значений: " + examples.size() + "\t\tпримеры: ");

				boolean first = true;
				int count = 0;
				for (String example : examples)
				{
					if (!first)
						out.append(", ");

					first = false;
					out.append(example);

					count++;
					if (count > 10)
					{
						out.append("...(" + (examples.size() - 10) + ")");
						break;
					}
				}
			}
			out.append("\r\n");
		}
	}

	private void processObject(int inner, String fieldName, JsonClassInfo jsonClassInfo, StringBuilder out)
	{
		for (int i = 0; i < inner-1; i++)
			out.append("- ");

		out.append(fieldName+"\r\n");
		
		printPrimitives(out, jsonClassInfo, inner);

		for (String innerObject : sort(jsonClassInfo.objects.keySet()))
		{
			processObject(inner+1, "["+innerObject+"]", jsonClassInfo.objects.get(innerObject), out);
		}
		
		for (String innerObject : sort(jsonClassInfo.arrays.keySet()))
		{
			processObject(inner+1, "[array] "+innerObject, jsonClassInfo.arrays.get(innerObject), out);
		}
	}

	private static List<String> sort(Collection<String> collection)
	{
		var sortedList = ListUtils.createList(collection);
		Collections.sort(sortedList);

		return sortedList;
	}
}
