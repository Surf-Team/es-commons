package ru.es.models;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import ru.es.lang.Converter;
import ru.es.log.Log;
import ru.es.util.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class XmlRepository
{
	public final File file;

	public Element rootXml;

	// предварительная обработка xml кода. Можно добавлять всякие условия
	public Converter<String, String> preProcessor;

	public XmlRepository(File file) throws Exception
	{
		this.file = file;
		reload();
	}

	public XmlRepository(File file, boolean autoLoad) throws Exception
	{
		this.file = file;

		if (autoLoad)
			reload();
	}

	public void reload() throws Exception
	{
		// preprocessor
		String text = new String(FileUtils.getBytes(file), StandardCharsets.UTF_8);
		if (preProcessor != null)
			text = preProcessor.convert(text);


		// read xml
		SAXBuilder parser = new SAXBuilder();
		InputStream inputStreamReader = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
		Document ret = parser.build(inputStreamReader);
		rootXml = ret.getRootElement();

		reloadImpl(rootXml);
	}

	protected abstract void reloadImpl(Element rootXml) throws Exception;

	public abstract void save() throws Exception;
}
