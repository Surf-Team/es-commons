package ru.es.jfx.xml.objects;

import ru.es.jfx.xml.IXmlObject;
import org.jdom2.Element;

import java.util.Collection;

/**
 * Created by saniller on 30.10.2016.
 */
public class XmlCollectionOfString implements IXmlObject
{
    String xmlName;
    Collection<String> collection;

    public XmlCollectionOfString(Collection<String> collection, String xmlName)
    {
        this.xmlName = xmlName;
        this.collection = collection;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element e = new Element(xmlName);
        for (String t : collection)
        {
            Element e1 = new Element("e");
            e.addContent(e1);
            e1.setAttribute("val", t);
        }
        return e;
    }

    @Override
    public void parseXml(Element data)
    {
        for (Element e : data.getChildren("e"))
        {
            collection.add(e.getAttributeValue("val"));
        }
    }

    @Override
    public String getXmlName()
    {
        return xmlName;
    }
}
