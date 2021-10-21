package ru.es.lang;

import org.jdom2.Element;

public interface XmlRecorder extends XmlReader
{
	void writeTo(Element e);

	@Override
	void readFrom(Element e);
}
