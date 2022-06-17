package ru.es.util;

import ru.es.lang.StringReplaceCall;
import ru.es.lang.Value;

public class HtmlUtils
{
	public static String replaceLangTag(String text, int lang)
	{
		String removeStart = "";
		String removeEnd = "";
		if (lang == 0)
		{
			removeStart = "<eng>"; // убираем всё, что в eng
			removeEnd = "</eng>";
		}
		else
		{
			removeStart = "<rus>";
			removeEnd = "</rus>";
		}

		text = removeInsideTag(text, removeStart, removeEnd);

		text = removeInsideTag(text, "<!--", "-->");

		String deleteStart = "";
		String deleteEnd = "";
		if (lang == 0)
		{
			deleteStart = "<rus>";
			deleteEnd = "</rus>";
		}
		else
		{
			deleteStart = "<eng>";
			deleteEnd = "</eng>";
		}
		text = text.replaceAll(deleteStart, "");
		text = text.replaceAll(deleteEnd, "");


		return text;
	}

	private static String removeInsideTag(String text, String removeStart, String removeEnd)
	{
		if (text.contains(removeStart))
		{
			for (int z =0; z < 70; z++)      // не более 70 тегов
			{
				int startRemoveIndex = text.indexOf(removeStart);
				if (startRemoveIndex == -1)
					break;

				String preString = text.substring(0, startRemoveIndex);

				int endIndex = text.indexOf(removeEnd, startRemoveIndex+1);
				if (endIndex > 0)
				{
					String postString = text.substring(endIndex+removeEnd.length(), text.length());
					text = preString + postString;
				}
				else
				{
					break;
				}
			}
		}
		return text;
	}

	// %include
	public static boolean replaceTag(Value<String> html, String startTag, String endTag, StringReplaceCall convert)
	{
		try
		{
			int includeIndex = html.get().indexOf(startTag);

			if (includeIndex == -1)
				return false;

			int endIndex = html.get().indexOf(endTag, includeIndex);

			String removeIt = html.get().substring(includeIndex, endIndex + endTag.length());

			String internalContent = removeIt.substring(startTag.length(), removeIt.length() - endTag.length());

			Value<String> replaceIt = new Value<>(internalContent);
			convert.call(replaceIt);

			html.set(
					html.get().replace(removeIt, replaceIt.get())
			);
			return true;
		}
		catch (Exception e)
		{
			// это уже ошибка html. Надо поправить, так что объявляем о ней в лог
			e.printStackTrace();
			return false;
		}
	}
}
