package ru.es.jfx.xml.objects;

import ru.es.lang.ESConstructor;
import ru.es.jfx.xml.IXmlObject;
import org.jdom2.Element;

import java.util.Collection;

/**
 * Created by saniller on 14.09.2016.
 */
public class XmlCollectionOfXmlitems<T extends IXmlObject> implements IXmlObject
{
    public Collection<T> XMLCollection;
    public String bankXmlName;
    public String itemXmlName;
    public ESConstructor<T, Element> constructor;


    public XmlCollectionOfXmlitems(Collection<T> XMLCollection, String bankXmlName, String itemXmlName, ESConstructor<T, Element> constructor)
    {
        this.XMLCollection = XMLCollection;
        this.bankXmlName = bankXmlName;
        this.itemXmlName = itemXmlName;
        this.constructor = constructor;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element ret = new Element(xmlName);
        for (T xmlObject : XMLCollection)
        {
            if (allowSave(xmlObject))
                ret.addContent(xmlObject.getXml(itemXmlName));

            onSaveItem();
        }
        return ret;
    }

    public boolean allowSave(T xmlObject)
    {
        return true;
    }

    @Override
    public void parseXml(Element data)
    {
        XMLCollection.clear();

        for (Element e : data.getChildren(itemXmlName))
        {
            T object = constructor.createObject(e);
            if (object != null)
            {
                XMLCollection.add(object);
            }
        }
        parsed();
    }

    public void onSaveItem()
    {

    }

    public void parsed()
    {

    }

    @Override
    public String getXmlName()
    {
        return bankXmlName;
    }
}
