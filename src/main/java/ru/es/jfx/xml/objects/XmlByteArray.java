package ru.es.jfx.xml.objects;

import ru.es.jfx.xml.IXmlObject;
import ru.es.jfx.xml.ESXmlUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;

/**
 * Created by saniller on 29.09.2016.
 */
public abstract class XmlByteArray implements IXmlObject
{
    String xmlName;

    public XmlByteArray(String bankXmlName)
    {
        this.xmlName = bankXmlName;
    }

    public abstract byte[] getArray();

    public abstract void setArray(byte[] newArray);


    @Override
    public Element getXml(String xmlName)
    {
        Element e = new Element(xmlName);

        String val = ESXmlUtils.byteArrayToAttributeValue(getArray());
        e.setAttribute("val", val);

        //Log.warning("Array: "+ ArrayUtils.byteToString(getArray()));

        return e;
    }

    @Override
    public void parseXml(Element data)
    {
        Attribute a = data.getAttribute("val");
        if (a != null)
        {
            String val = a.getValue();
            byte[] ret = ESXmlUtils.attributeValueToByteArray(val);
            setArray(ret);
        }


        //Log.warning("Array: "+ ArrayUtils.byteToString(getArray()));
    }

    @Override
    public String getXmlName()
    {
        return xmlName;
    }
}

