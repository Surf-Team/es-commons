package ru.es.fileCache;

public interface OnIncludeHtmlEvent<T>
{
	String onEvent(String html, T exchange);
}
