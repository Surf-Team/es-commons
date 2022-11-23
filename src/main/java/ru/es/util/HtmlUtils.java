package ru.es.util;

import org.apache.commons.lang3.tuple.Pair;
import ru.es.lang.StringReplaceCall;
import ru.es.lang.Value;
import ru.es.lang.table.Entry;

public class HtmlUtils
{
	public static String replaceLangTag(String ret, int lang)
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

		ret = removeInsideTag(ret, removeStart, removeEnd);

		ret = removeInsideTag(ret, "<!--", "-->");

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
		ret = ret.replaceAll(deleteStart, "");
		ret = ret.replaceAll(deleteEnd, "");


		// short version (old)
		Value<String> htmlVal = new Value<>(ret);
		while (true)
		{
			boolean ok = HtmlUtils.replaceTag(htmlVal, "printL(", ");", tagContent ->
			{
				String content = tagContent.get();
				String[] split = content.split("\"");

				String[] args = { split[1], split[3] };

				String text = args[lang].trim();
				tagContent.set(text);
				return true;
			});

			if (!ok)
				break;
		}
		ret = htmlVal.get();


		return ret;
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

	public static String hrefArgs(String baseHref, Entry... entries)
	{
		String ret = baseHref;
		ret += "?";

		boolean first = true;
		for (Entry e : entries)
		{
			if (!first)
				ret+="&";
			ret += e.key +"="+e.value;

			first = false;
		}
		return ret;
	}

	public static String button(String name, String baseHref, Entry... args)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("<a class='button' href='");
		ret.append(hrefArgs(baseHref, args));
		ret.append("'>");
		ret.append(name);
		ret.append("</a>");
		return ret.toString();
	}

	public static String a(String name, String baseHref, Entry... args)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("<a href='");
		ret.append(hrefArgs(baseHref, args));
		ret.append("'>");
		ret.append(name);
		ret.append("</a>");
		return ret.toString();
	}

	public static String a(String name, String cssClass, String baseHref, Entry... args)
	{
		StringBuilder ret = new StringBuilder();
		ret.append("<a class=\""+cssClass+"\" href='");
		ret.append(hrefArgs(baseHref, args));
		ret.append("'>");
		ret.append(name);
		ret.append("</a>");
		return ret.toString();
	}

	public static String getPaginationLinks(int numberOfDataLines, int numberOfLines)
	{
		StringBuilder sb = new StringBuilder();
		int count;

		if (numberOfLines > 0 && numberOfDataLines % numberOfLines == 0)
		{
			count = numberOfDataLines / numberOfLines;
		}
		else
		{
			count = numberOfDataLines / numberOfLines + 1;
		}

		if (count == 1)
			return "";
		for (int i = 1; i <= count; i++)
		{
			sb.append("<span onclick='showPagination(").append(i).append(")' class='custom-link'>< ")
					.append(i).append(" > </span>");
		}

		return sb.toString();
	}

	public static String getPaginationLinks(int numberOfDataLines, int numberOfLines, String arg)
	{
		StringBuilder sb = new StringBuilder();
		int count;

		if (numberOfLines > 0 && numberOfDataLines % numberOfLines == 0)
		{
			count = numberOfDataLines / numberOfLines;
		}
		else
		{
			count = numberOfDataLines / numberOfLines + 1;
		}

		for (int i = 1; i <= count; i++)
		{
			sb.append("<span onclick='showPagination(").append(i).append(", \"").append(arg)
					.append("\")' class='custom-link'>< ").append(i).append(" > </span>");
		}

		return sb.toString();
	}
}
