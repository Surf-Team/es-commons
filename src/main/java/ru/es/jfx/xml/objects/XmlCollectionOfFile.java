package ru.es.jfx.xml.objects;

import ru.es.jfx.xml.IXmlObject;
import org.jdom2.Element;

import java.io.File;
import java.util.Collection;

/**
 * Created by saniller on 21.12.2016.
 */
public class XmlCollectionOfFile implements IXmlObject
{
    String xmlName;
    Collection<File> collection;
    Collection<File> defaultValue;

    public XmlCollectionOfFile(Collection<File> collection, String xmlName, Collection<File> defaultValue)
    {
        this.xmlName = xmlName;
        this.collection = collection;
        this.defaultValue = defaultValue;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element e = new Element(xmlName);
        for (File t : collection)
        {
            Element e1 = new Element("e");
            e.addContent(e1);
            e1.setAttribute("val", t.getAbsolutePath());
        }
        return e;
    }

    @Override
    public void parseXml(Element data)
    {
        for (Element e : data.getChildren("e"))
        {
            collection.add(new File(e.getAttributeValue("val")));
        }

        if (collection.isEmpty())
            collection.addAll(defaultValue);
    }

    @Override
    public String getXmlName()
    {
        return xmlName;
    }
}
