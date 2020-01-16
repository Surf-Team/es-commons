package ru.es.jfx.xml.objects;

import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.xml.IXmlObject;
import ru.es.util.ListUtils;
import javafx.collections.ObservableList;
import org.jdom2.Element;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by saniller on 14.09.2016.
 */
public class XmlCollectionOfIntegerProperty implements IXmlObject
{
    ObservableList<ESProperty<Integer>> integerObservableList;
    String xmlName;

    public XmlCollectionOfIntegerProperty(ObservableList<ESProperty<Integer>> integerObservableList, String xmlName)
    {
        this.integerObservableList = integerObservableList;
        this.xmlName = xmlName;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element ret = new Element(xmlName);
        List<Integer> ints = new LinkedList<Integer>();

        for (ESProperty<Integer> integer : integerObservableList)
        {
            ints.add(integer.get());
        }

        ret.setAttribute("list", ListUtils.getStringFromList(ints, ","));
        return ret;
    }

    @Override
    public void parseXml(Element data)
    {
        String val = data.getAttributeValue("list");
        List<Integer> ints = ListUtils.getListOfInt(val);
        List<ESProperty<Integer>> intProps = new LinkedList<ESProperty<Integer>>();
        for (Integer i : ints)
        {
            intProps.add(new ESProperty<>(i));
        }
        integerObservableList.setAll(intProps);
    }

    @Override
    public String getXmlName()
    {
        return xmlName;
    }
}
