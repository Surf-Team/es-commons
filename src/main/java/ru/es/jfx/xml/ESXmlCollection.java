package ru.es.jfx.xml;

import org.jdom2.Element;

/**
 * Created by saniller on 25.07.2016.
 */
public interface ESXmlCollection
{
    // старый тип коллекций, когда все элементы лежат непосредственно в root. Не использовать

    void parseXml(Element rootContainer);

    void appendXml(Element to);

    String getXmlName();
}
