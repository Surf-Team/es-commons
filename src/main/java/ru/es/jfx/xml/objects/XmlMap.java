package ru.es.jfx.xml.objects;

import ru.es.lang.ESXmlBackwardConstructor;
import ru.es.jfx.xml.IXmlObject;
import org.jdom2.Element;
import ru.es.log.Log;

import java.util.Map;

/**
 * Created by saniller on 14.09.2016.
 */
public class XmlMap<K,V> implements IXmlObject
{
    String xmlName;
    Map<K,V> map;
    public ESXmlBackwardConstructor<K> keyConstructor;
    public ESXmlBackwardConstructor<V> valueConstructor;

    public XmlMap(String xmlName, Map<K, V> map, ESXmlBackwardConstructor<K> keyConstructor, ESXmlBackwardConstructor<V> valueConstructor)
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
            Element elementContent = null;
            try
            {
                elementContent = keyConstructor.createElement(entry.getKey(), "k");
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                Log.warning("Ошибка с элементом. Не обращаем внимания.");
                continue;
            }
            if (elementContent == null)
            {
                Log.warning("XmlMap: Element (key) cannot be null! "+getXmlName());
            }
            entryElement.addContent(elementContent);
            Element valueContent = valueConstructor.createElement(entry.getValue(), "v");
            if (valueContent == null)
            {
                Log.warning("XmlMap: Element (value) cannot be null! "+getXmlName());
            }
            entryElement.addContent(valueContent);
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
            Element key = e.getChild("k");
            Element value = e.getChild("v");
            K keyObject = keyConstructor.createObject(key);
            if (keyObject == null)
                continue;

            V valueObject = createValue(keyObject, value);
            if (valueObject == null)
                continue;

            map.put(keyObject, valueObject);
        }
        onParse();
    }

    public V createValue(K key, Element value)
    {
        return valueConstructor.createObject(value);
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