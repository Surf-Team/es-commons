package ru.es.models;

import org.jdom2.Element;
import ru.es.util.FileUtils;

import java.io.File;

public abstract class XmlRepository
{
	public final File file;

	public Element rootXml;

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
		rootXml = FileUtils.getXmlDocument(file);
		reloadImpl(rootXml);
	}

	protected abstract void reloadImpl(Element rootXml) throws Exception;

	public abstract void save() throws Exception;
}
