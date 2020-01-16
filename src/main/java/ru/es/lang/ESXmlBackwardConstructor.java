package ru.es.lang;

import org.jdom2.Element;

/**
 * Created by saniller on 14.09.2016.
 */
public interface ESXmlBackwardConstructor<NewObject>
{
    public NewObject createObject(Element e);

    public Element createElement(NewObject fromThat, String elementName);
}
