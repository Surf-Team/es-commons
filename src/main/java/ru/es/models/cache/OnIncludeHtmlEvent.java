package ru.es.models.cache;

public interface OnIncludeHtmlEvent<T>
{
	String onEvent(String html, T exchange);
}
