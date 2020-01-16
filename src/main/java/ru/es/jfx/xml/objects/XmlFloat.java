package ru.es.jfx.xml.objects;

import ru.es.jfx.xml.IXmlObject;
import javafx.beans.property.Property;
import org.jdom2.Element;

/**
 * Created by saniller on 23.12.2016.
 */
public class XmlFloat implements IXmlObject
{
    final String xmlName;
    public Property<Float> numberProperty;

    public XmlFloat(Property<Float> number, final String xmlName)
    {
        this.xmlName = xmlName;
        this.numberProperty = number;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element ret = new Element(xmlName);
        ret.setAttribute("val", "" + numberProperty.getValue());
        return ret;
    }

    @Override
    public void parseXml(Element data)
    {
        numberProperty.setValue(Float.parseFloat(data.getAttributeValue("val")));
    }

    @Override
    public String getXmlName()
    {
        return xmlName;
    }
}

