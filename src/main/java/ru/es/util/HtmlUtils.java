package ru.es.util;

import ru.es.lang.Converter;
import ru.es.lang.StringReplaceCall;
import ru.es.lang.Value;
import ru.es.fileCache.table.Entry;
import ru.es.log.Log;

import java.util.Set;

//todo перенести всё что касается тэгов в какой нибудь HtmlTagUtils
// затем перенести HtmlUtils в модуль ESHttpServer
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
			// не более 70 тегов, изменено на 450 08.09.2023
			// изменено обратно на 100, т.к. если 450, значит где то в архитектуре проблема. Нужно заменять тэги на более ранней стадии, использовать кэш, и прочее
			for (int z = 0; z < 100; z++)
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

				if (z == 99)
				{
					Log.warning("Потенциальная утечка производительности. Нужно устранить. ");
					Log.warning("Файл: "+text);
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


	// метод для замены тэгов в тексте с параметрами
	// пример тега в тексте: htmlfile(index.html)
	public static String replaceTag(String html, String startTag, String endTag, Converter<String, String> convert)
	{
		String ret = html;
		try
		{
			int limit = 100;
			while (true)
			{
				limit--;
				if (limit <= 0) // anti-deadlock
				{
					Log.warning("Anti-deadlock stop replace tag");
					break;
				}
				int includeIndex = html.indexOf(startTag);

				if (includeIndex == -1)
					break;

				int endIndex = html.indexOf(endTag, includeIndex);

				String removeIt = html.substring(includeIndex, endIndex + endTag.length());

				String internalContent = removeIt.substring(startTag.length(), removeIt.length() - endTag.length());

				String replaced = convert.convert(internalContent);

				if (!ret.contains(removeIt))
					break;
				
				ret = ret.replace(removeIt, replaced);
			}
		}
		catch (Exception e)
		{
			// это уже ошибка html. Надо поправить, так что объявляем о ней в лог
			e.printStackTrace();
		}
		return ret;
	}

	public static String hrefArgs(String baseHref, Entry... entries)
	{
		String ret = baseHref;

		if (entries.length != 0)
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

	public static String createStringWithHref(String text)
	{
		text = text.replace("\n", " <br> ");
		text = text.replaceAll("\\s+", " ");
		String[] split = text.split(" ");
		StringBuilder sb = new StringBuilder();

		for (String s : split)
		{
			if (s.startsWith("http"))
			{
				s = "<a target='_blank' href='" + s + "'>" + s + "</a>";
			}
			sb.append(s).append(" ");
		}

		return sb.toString();
	}

	public static String getPaginationLinks(String apiUrlPagination, int numberOfDataLines, int numberOfLines, String arg)
	{
		StringBuilder sb = new StringBuilder();
		int count = getCountPaginationLinks(numberOfDataLines, numberOfLines);

		if (count == 1)
			return "";

		for (int i = 1; i <= count; i++)
		{
			sb.append("<span onclick='showPagination(\".");
			sb.append(apiUrlPagination);
			sb.append("\", ");
			sb.append(i);
			if (arg == null || arg.equals(""))
			{
				sb.append(")' class='custom-link'>< ");
			}
			else
			{
				sb.append(", \"");
				sb.append(arg);
				sb.append("\")' class='custom-link'>< ");
			}
			sb.append(i);
			sb.append(" > </span>");
		}

		return sb.toString();
	}

	public static String getPaginationLinks(String apiUrlPagination, int numberOfDataLines, int numberOfLines,
											String arg, boolean castForGveIOSite)
	{
		StringBuilder sb = new StringBuilder();
		int count = getCountPaginationLinks(numberOfDataLines, numberOfLines);

		if (count == 1)
			return "";

		for (int i = 1; i <= count; i++)
		{
			if (arg == null || arg.equals(""))
			{
				if (castForGveIOSite)
				{
					sb.append("<span onclick='showPagination(\".");
					sb.append(apiUrlPagination);
					sb.append("\", ");
					sb.append(i);
					sb.append(")' class='custom-link'>< ");
					sb.append(i);
					sb.append(" > </span>");
				}
				else
				{
					sb.append("<li id=\"");
					sb.append(i);
					if (i == 1)
					{
						sb.append("\" class=\"page-item active\"><span class=\"page-link lib-cursor-pointer\" onclick='showPagination(");
					}
					else
					{
						sb.append("\" class=\"page-item\"><span class=\"page-link lib-cursor-pointer\" onclick='showPagination(");
					}
					sb.append("\".");
					sb.append(apiUrlPagination);
					sb.append("\", ");
					sb.append(i);
					sb.append(")'>");
					sb.append(i);
					sb.append("</span></li>");
				}
			}
			else
			{
				if (castForGveIOSite)
				{
					sb.append("<span onclick='showPagination(\".");
					sb.append(apiUrlPagination);
					sb.append("\", ");
					sb.append(i);
					sb.append(", \"");
					sb.append(arg);
					sb.append("\")' class='custom-link'>< ");
					sb.append(i);
					sb.append(" > </span>");
				}
				else
				{
					sb.append("<li id=\"");
					sb.append(i);
					if (i == 1)
					{
						sb.append("\" class=\"page-item active\"><span class=\"page-link lib-cursor-pointer\" onclick='showPagination(");
					}
					else
					{
						sb.append("\" class=\"page-item\"><span class=\"page-link lib-cursor-pointer\" onclick='showPagination(");
					}
					sb.append("\".");
					sb.append(apiUrlPagination);
					sb.append("\", ");
					sb.append(i);
					sb.append(", \"");
					sb.append(arg);
					sb.append("\")'>");
					sb.append(i);
					sb.append("</span></li>");
				}
			}
		}

		return sb.toString();
	}

	public static int getCountPaginationLinks(int numberOfDataLines, int numberOfLines)
	{
		int count;
		if (numberOfLines > 0 && numberOfDataLines % numberOfLines == 0)
		{
			count = numberOfDataLines / numberOfLines;
		}
		else
		{
			count = numberOfDataLines / numberOfLines + 1;
		}
		return count;
	}

	public static String getPaginationLinks(String apiUrlPagination, Set<Character> characterIds)
	{
		StringBuilder sb = new StringBuilder();

		for(Character cId : characterIds)
		{
			sb.append("<li id=\"");
			sb.append(cId);
			sb.append("\" class=\"page-item\"><span class=\"page-link lib-cursor-pointer\" onclick='libShowCharPagination(\".");
			sb.append(apiUrlPagination);
			sb.append("\", \"");
			sb.append(cId);
			sb.append("\")'>");
			sb.append(cId);
			sb.append("</span></li>");
		}
		return sb.toString();
	}
}
