package ru.es.annotation;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class MappedXML
{
	public File file;
	public Map<String, Element> idMap = new LinkedHashMap<>();
	public Element root;

	public MappedXML(File file, String idAttribute) throws IOException, JDOMException
	{
		this.file = file;

		root = FileUtils.getXmlDocument(file);
		root.detach();

		for (Element e : root.getChildren())
		{
			String id = e.getAttributeValue(idAttribute);
			idMap.put(id, e);
		}
	}

	public MappedXML(Element root, String idAttribute) throws IOException, JDOMException
	{
		this.file = null;
		this.root = root;
		this.root.detach();

		for (Element e : this.root.getChildren())
		{
			String id = e.getAttributeValue(idAttribute);
			idMap.put(id, e);
		}
	}
	public MappedXML(URL file, String idAttribute) throws IOException, JDOMException
	{
		root = FileUtils.getXmlDocument(file);
		root.detach();

		for (Element e : root.getChildren())
		{
			String id = e.getAttributeValue(idAttribute);
			idMap.put(id, e);
		}
	}

	public Element getById(String id)
	{
		return idMap.get(id);
	}

	public void save() throws IOException
	{
		FileUtils.saveXmlDocWideFormat(root, file);
	}
}
