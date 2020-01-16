package ru.es.jfx.xml;

import ru.es.lang.ESConstructor;
import ru.es.util.ListUtils;
import org.jdom2.Attribute;
import org.jdom2.Element;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by saniller on 02.05.2016.
 */
public class ESXmlUtils
{
    public static Element stringArrayToXml(String[] array, String xmlName)
    {
        Element e = new Element(xmlName);
        for (String s : array)
        {
            Element xml = new Element("obj");
            xml.setAttribute("str", s);
            e.addContent(xml);
        }
        return e;
    }

    public static String[] xmlToArrayString(Element xml)
    {
        List<String> retList = new LinkedList<>();
        for (Element e : xml.getChildren("obj"))
        {
            Attribute a = e.getAttribute("str");
            if (a != null)
                retList.add(a.getValue());
        }
        return ListUtils.listToArray(retList, new String[retList.size()]);
    }

    // сохраняет значения через toString()
    public static<T> Element collectionToElement(Collection<T> collection, String elementName)
    {
        Element e = new Element(elementName);

        for (T obj : collection)
        {
            Element el = new Element("val");
            el.setAttribute("att", obj.toString());
            e.addContent(el);
        }

        return e;
    }


    public static<T> List<T> elementToCollection(Element e, ESConstructor<T, String> constructor)
    {
        List<T> ret = new LinkedList<>();
        for (Element el : e.getChildren("val"))
        {
            String value = el.getAttributeValue("att");

            try
            {
                T obj = constructor.createObject(value);
                ret.add(obj);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return ret;
    }

    public static String byteArrayToAttributeValue(byte[] array)
    {
        if (array == null)
            return "null";

        StringBuilder builder = new StringBuilder((int) (array.length*2 * 1.5));

        int i = 0;
        for (byte b : array)
        {
            i++;
            if (i == array.length)
            {
                builder.append(b);
                break;
            }
            else
                builder.append(b+",");
        }

        //String ret = new String(array); для xml не пойдёт

        return builder.toString();
    }


    public static byte[] attributeValueToByteArray(String attributeValue)
    {
        if (attributeValue.equals("null") || attributeValue.isEmpty())
            return null;

        String[] strArray = attributeValue.split(",");
        byte[] ret = new byte[strArray.length];
        for (int i = 0; i < strArray.length; i++)
        {
            String parse = strArray[i];
            if (parse.isEmpty())
                continue;
            ret[i] = Byte.parseByte(parse);
        }
        return ret;
    }



    public static String intArrayToAttributeValue(int[] array)
    {
        //todo медленный! делать как выше у byte через StringBuilder

        if (array == null)
            return "null";

        String ret = "";
        int i = 0;
        for (int b : array)
        {
            ret += b;
            i++;
            if (i == array.length)
                break;

            ret +=",";
        }
        return ret;
    }

    public static int[] attributeValueToIntArray(String attributeValue)
    {
        if (attributeValue.equals("null"))
            return null;

        String[] strArray = attributeValue.split(",");
        int[] ret = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++)
        {
            ret[i] = Integer.parseInt(strArray[i]);
        }
        return ret;
    }

    public static void clearXmlFully(Element xml)
    {
        for (Element ch1 : xml.getChildren())
        {
            //Log.warning("ch1");
            for (Element ch2 : ch1.getChildren())
            {
                //Log.warning("ch2");
                for (Element ch3 : ch2.getChildren())
                {
                    //Log.warning("ch3");
                    for (Element ch4 : ch3.getChildren())
                    {
                        //Log.warning("ch4");
                        for (Element ch5 : ch4.getChildren())
                        {
                            //Log.warning("ch5");
                            for (Element ch6 : ch5.getChildren())
                            {
                                //Log.warning("ch6");
                                for (Element ch7 : ch6.getChildren())
                                {
                                    //Log.warning("ch7");
                                    for (Element ch8 : ch7.getChildren())
                                    {
                                        //Log.warning("ch8");
                                        for (Element ch9 : ch8.getChildren())
                                        {
                                            //Log.warning("ch9");
                                            for (Element ch10 : ch9.getChildren())
                                            {
                                                //Log.warning("ch10");
                                                for (Element ch11 : ch10.getChildren())
                                                {
                                                    //Log.warning("ch11");
                                                    for (Element ch12 : ch11.getChildren())
                                                    {
                                                        //Log.warning("ch12");
                                                        for (Element ch13 : ch12.getChildren())
                                                        {
                                                            //Log.warning("ch13");
                                                            ch13.removeContent();
                                                            ch13.removeChildren(null);
                                                            while (!ch13.getAttributes().isEmpty())
                                                                ch13.removeAttribute(ch13.getAttributes().get(0));
                                                        }
                                                        ch12.removeContent();
                                                        ch12.removeChildren(null);
                                                        while (!ch12.getAttributes().isEmpty())
                                                            ch12.removeAttribute(ch12.getAttributes().get(0));
                                                    }
                                                    ch11.removeContent();
                                                    ch11.removeChildren(null);
                                                    while (!ch11.getAttributes().isEmpty())
                                                        ch11.removeAttribute(ch11.getAttributes().get(0));
                                                }
                                                ch10.removeContent();
                                                ch10.removeChildren(null);
                                                while (!ch10.getAttributes().isEmpty())
                                                    ch10.removeAttribute(ch10.getAttributes().get(0));
                                            }
                                            ch9.removeContent();
                                            ch9.removeChildren(null);
                                            while (!ch9.getAttributes().isEmpty())
                                                ch9.removeAttribute(ch9.getAttributes().get(0));
                                        }
                                        ch8.removeContent();
                                        ch8.removeChildren(null);
                                        while (!ch8.getAttributes().isEmpty())
                                            ch8.removeAttribute(ch8.getAttributes().get(0));
                                    }
                                    ch7.removeContent();
                                    ch7.removeChildren(null);
                                    while (!ch7.getAttributes().isEmpty())
                                        ch7.removeAttribute(ch7.getAttributes().get(0));
                                }
                                ch6.removeContent();
                                ch6.removeChildren(null);
                                while (!ch6.getAttributes().isEmpty())
                                    ch6.removeAttribute(ch6.getAttributes().get(0));
                            }
                            ch5.removeContent();
                            ch5.removeChildren(null);
                            while (!ch5.getAttributes().isEmpty())
                                ch5.removeAttribute(ch5.getAttributes().get(0));
                        }
                        ch4.removeContent();
                        ch4.removeChildren(null);
                        while (!ch4.getAttributes().isEmpty())
                            ch4.removeAttribute(ch4.getAttributes().get(0));
                    }
                    ch3.removeContent();
                    ch3.removeChildren(null);
                    while (!ch3.getAttributes().isEmpty())
                        ch3.removeAttribute(ch3.getAttributes().get(0));
                }
                ch2.removeContent();
                ch2.removeChildren(null);
                while (!ch2.getAttributes().isEmpty())
                    ch2.removeAttribute(ch2.getAttributes().get(0));
            }
            ch1.removeContent();
            ch1.removeChildren(null);
            while (!ch1.getAttributes().isEmpty())
                ch1.removeAttribute(ch1.getAttributes().get(0));
        }
        xml.removeContent();
        xml.removeChildren(null);
        while (!xml.getAttributes().isEmpty())
            xml.removeAttribute(xml.getAttributes().get(0));

        xml = null;
    }
}
