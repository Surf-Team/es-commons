package ru.es.jfx.xml;

import org.jdom2.Element;

/**
 * Created by saniller on 17.08.2016.
 */
public interface IXmlObject
{
    Element getXml(String xmlName);

    void parseXml(Element data);

    String getXmlName();

    default boolean alertIfNotFound()
    {
        return true;
    }

    default int parsePriority()
    {
        return 0;
    }

}
