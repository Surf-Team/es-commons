package ru.es.jfx.xml.objects;

import ru.es.jfx.xml.IXmlObject;
import org.jdom2.Element;

import java.util.Collection;

/**
 * Created by saniller on 01.11.2016.
 */
public class XmlCollectionOfNumber<T extends Number> implements IXmlObject
{
    String xmlName;
    Collection<T> collection;
    Class<T> numberClass;

    public XmlCollectionOfNumber(Collection<T> collection, Class<T> numberClass, String xmlName)
    {
        this.xmlName = xmlName;
        this.collection = collection;
        this.numberClass = numberClass;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element e = new Element(xmlName);
        for (T t : collection)
        {
            Element e1 = new Element("e");
            e.addContent(e1);
            if (numberClass == Integer.class)
                e1.setAttribute("val", ""+t.intValue());
            else if (numberClass == Long.class)
                e1.setAttribute("val", ""+t.longValue());
            else if (numberClass == Double.class)
                e1.setAttribute("val", ""+t.doubleValue());
            else if (numberClass == Float.class)
                e1.setAttribute("val", ""+t.floatValue());
            else if (numberClass == Short.class)
                e1.setAttribute("val", ""+t.shortValue());
            else if (numberClass == Byte.class)
                e1.setAttribute("val", ""+t.byteValue());
        }
        return e;
    }

    @Override
    public void parseXml(Element data)
    {
        try
        {
            Collection c = collection;
            for (Element e : data.getChildren("e"))
            {
                if (numberClass == Integer.class)
                    c.add(e.getAttribute("val").getIntValue());
                else if (numberClass == Long.class)
                    c.add(e.getAttribute("val").getLongValue());
                else if (numberClass == Double.class)
                    c.add(e.getAttribute("val").getDoubleValue());
                else if (numberClass == Float.class)
                    c.add(e.getAttribute("val").getFloatValue());
                else if (numberClass == Short.class)
                    c.add((short) e.getAttribute("val").getIntValue());
                else if (numberClass == Byte.class)
                    c.add((byte) e.getAttribute("val").getIntValue());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public String getXmlName()
    {
        return xmlName;
    }
}
