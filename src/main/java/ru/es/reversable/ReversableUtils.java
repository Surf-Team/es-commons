package ru.es.reversable;

import ru.es.jfx.xml.IXmlObject;
import ru.es.thread.RunnableImpl;
import javolution.util.FastMap;
import org.jdom2.Element;

import java.util.List;
import java.util.Map;


/**
 * Created by saniller on 29.03.2017.
 */
public class ReversableUtils
{
    public static IReversableFunction makeForXML(IXmlObject xmlObject, Runnable doFunction)
    {
        Element oldXml = xmlObject.getXml("Reverse");

        return new IReversableFunction()
        {
            Element newXml;
            @Override
            public void doFunction()
            {
                if (doFunction != null)
                    doFunction.run();

                newXml = xmlObject.getXml("forRedo");
            }

            @Override
            public void redoFunction()
            {
                xmlObject.parseXml(newXml);
            }

            @Override
            public void undoFunction()
            {
                xmlObject.parseXml(oldXml);
            }
        };
    }

    public static ReversableFunction makeForXML(List<IXmlObject> xmlObjects)
    {
        Map<IXmlObject, Element> oldXmls = new FastMap<>();

        for (IXmlObject o : xmlObjects)
            oldXmls.put(o, o.getXml("Reverse"));

        return new ReversableFunction()
        {
            Map<IXmlObject, Element> newXmls = new FastMap<>();
            @Override
            public void doFunction()
            {
                for (IXmlObject o : xmlObjects)
                {
                    newXmls.put(o, o.getXml("forRedo"));
                }
            }

            @Override
            public void redoFunction()
            {
                for (IXmlObject o : xmlObjects)
                {
                    o.parseXml(newXmls.get(o));
                }
            }

            @Override
            public void undoFunction()
            {
                for (IXmlObject o : xmlObjects)
                {
                    o.parseXml(oldXmls.get(o));
                }
            }
        };
    }

    public static ReversableFunction makeForXML(List<IXmlObject> xmlObjects, RunnableImpl onRedoUndo)
    {
        Map<IXmlObject, Element> oldXmls = new FastMap<>();

        for (IXmlObject o : xmlObjects)
            oldXmls.put(o, o.getXml("Reverse"));

        return new ReversableFunction()
        {
            Map<IXmlObject, Element> newXmls = new FastMap<>();
            @Override
            public void doFunction()
            {
                for (IXmlObject o : xmlObjects)
                {
                    newXmls.put(o, o.getXml("forRedo"));
                }
            }

            @Override
            public void redoFunction()
            {
                for (IXmlObject o : xmlObjects)
                {
                    o.parseXml(newXmls.get(o));
                }
                onRedoUndo.run();
            }

            @Override
            public void undoFunction()
            {
                for (IXmlObject o : xmlObjects)
                {
                    o.parseXml(oldXmls.get(o));
                }
                onRedoUndo.run();
            }
        };
    }
}
