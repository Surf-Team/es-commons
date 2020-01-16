package ru.es.jfx.xml.objects;

import ru.es.jfx.xml.IXmlObject;
import javafx.beans.property.Property;
import org.jdom2.Element;

/**
 * Created by saniller on 12.10.2016.
 */
public class XmlBoolean implements IXmlObject
{
    Property<Boolean> booleanProperty;
    String xmlName;

    public XmlBoolean(Property<Boolean> booleanProperty, String xmlName)
    {
        this.booleanProperty = booleanProperty;
        this.xmlName = xmlName;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element ret = new Element(xmlName);
        ret.setAttribute("val", ""+booleanProperty.getValue());
        return ret;
    }

    @Override
    public void parseXml(Element data)
    {
        booleanProperty.setValue(Boolean.parseBoolean(data.getAttributeValue("val")));
    }

    @Override
    public String getXmlName()
    {
        return xmlName;
    }
}
