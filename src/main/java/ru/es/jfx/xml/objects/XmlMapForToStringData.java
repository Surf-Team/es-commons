package ru.es.jfx.xml.objects;

import ru.es.lang.ESConstructor;
import ru.es.jfx.xml.IXmlObject;
import org.jdom2.Element;

import java.util.Map;

/**
 * Created by saniller on 14.09.2016.
 */
public class XmlMapForToStringData<K,V> implements IXmlObject
{
    String xmlName;
    Map<K,V> map;
    ESConstructor<K, String> keyConstructor;
    ESConstructor<V, String> valueConstructor;

    public XmlMapForToStringData(String xmlName, Map<K, V> map, ESConstructor<K, String> keyConstructor, ESConstructor<V, String> valueConstructor)
    {
        this.xmlName = xmlName;
        this.map = map;
        this.keyConstructor = keyConstructor;
        this.valueConstructor = valueConstructor;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element e = new Element(xmlName);

        for (Map.Entry<K, V> entry : map.entrySet())
        {
            Element entryElement = new Element("e");
            entryElement.setAttribute("k", ""+entry.getKey());
            entryElement.setAttribute("v", ""+entry.getValue());
            e.addContent(entryElement);
        }

        return e;
    }

    @Override
    public void parseXml(Element data)
    {
        map.clear();
        for (Element e : data.getChildren("e"))
        {
            String key = e.getAttributeValue("k");
            String value = e.getAttributeValue("v");
            K keyObject = keyConstructor.createObject(key);
            V valueObject = valueConstructor.createObject(value);
            map.put(keyObject, valueObject);
        }
        onParse();
    }

    public void onParse()
    {

    }

    @Override
    public String getXmlName()
    {
        return xmlName;
    }
}
