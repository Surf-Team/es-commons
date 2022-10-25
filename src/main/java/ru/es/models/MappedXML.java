package ru.es.models;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import ru.es.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MappedXML
{
	private File file;
	private Map<String, Element> idMap = new HashMap<>();
	private Element root;

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

	public Element getById(String id)
	{
		return idMap.get(id);
	}

	public void save() throws IOException
	{
		FileUtils.saveXmlDocWideFormat(root, file);
	}
}
