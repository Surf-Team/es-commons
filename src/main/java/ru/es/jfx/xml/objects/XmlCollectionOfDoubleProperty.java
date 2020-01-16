package ru.es.jfx.xml.objects;

import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.xml.IXmlObject;
import ru.es.util.ListUtils;
import javafx.collections.ObservableList;
import org.jdom2.Element;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by saniller on 11.12.2016.
 */
public class XmlCollectionOfDoubleProperty implements IXmlObject
{
    ObservableList<ESProperty<Double>> integerObservableList;
    String xmlName;

    public XmlCollectionOfDoubleProperty(ObservableList<ESProperty<Double>> integerObservableList, String xmlName)
    {
        this.integerObservableList = integerObservableList;
        this.xmlName = xmlName;
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element ret = new Element(xmlName);
        List<Double> ints = new LinkedList<Double>();

        for (ESProperty<Double> n : integerObservableList)
        {
            ints.add(n.get());
        }

        ret.setAttribute("list", ListUtils.getStringFromList(ints, ","));
        return ret;
    }

    @Override
    public void parseXml(Element data)
    {
        String val = data.getAttributeValue("list");
        List<Double> ints = ListUtils.getListOfDouble(val);
        List<ESProperty<Double>> intProps = new LinkedList<ESProperty<Double>>();
        for (Double i : ints)
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
