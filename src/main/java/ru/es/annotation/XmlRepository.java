package ru.es.annotation;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import ru.es.lang.Converter;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public abstract class XmlRepository
{
	public final URL file;

	public Element rootXml;

	// предварительная обработка xml кода. Можно добавлять всякие условия
	public Converter<String, String> preProcessor;

	public XmlRepository(URL file) throws Exception
	{
		this.file = file;
		reload();
	}

	public XmlRepository(URL file, boolean autoLoad) throws Exception
	{
		this.file = file;

		if (autoLoad)
			reload();
	}
	public XmlRepository(File file) throws Exception
	{
		this.file = file.toURI().toURL();
		reload();
	}

	public XmlRepository(File file, boolean autoLoad) throws Exception
	{
		this.file = file.toURI().toURL();

		if (autoLoad)
			reload();
	}

	public void reload() throws Exception
	{
		// preprocessor
		var stream = file.openStream();
		byte[] bytes = stream.readAllBytes();
		stream.close();

		String text = new String(bytes, StandardCharsets.UTF_8);
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
