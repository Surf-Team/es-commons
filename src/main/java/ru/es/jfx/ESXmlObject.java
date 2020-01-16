package ru.es.jfx;

//import com.sun.istack.internal.NotNull;
//import com.sun.javafx.collections.ObservableListWrapper;
//import com.sun.javafx.collections.ObservableSetWrapper;
import ru.es.lang.ESConstructor;
import ru.es.lang.ESValue;
import ru.es.jfx.binding.ESProperty;
import javafx.beans.property.Property;
import javafx.scene.paint.Color;
import javolution.util.FastMap;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Parent;
import ru.es.jfx.xml.ESXmlCollection;
import ru.es.jfx.xml.IXmlObject;
import ru.es.log.Log;

import java.io.File;
import java.util.*;

// класс позволяет удобно организовывать дерево объектов, сохраняемых в XML и загружаемых из него.
// для объявления сохраняемого объекта достаточно объявить любой объект из вложенных классов.
public abstract class ESXmlObject implements IXmlObject
{
    // если нужно загружать последовательно:
    // например если сначала нужно выбрать главный существующий объект, т.к. остальные потом операются на результат, то
    // первый в очереди - AttributeObject. Сами эти объекты выполняются по очереди в порядке от самого верхнего в классе к нижнему (по загрузке полей объекта)

    // задаётся при parseXml
    private Element rootXml;

    // значения автоматически добавляются сюда
    private LinkedList<ISettingValue> valueHolder = new LinkedList<ISettingValue>();
    private LinkedList<SettingAttributeObject> attributeObjHolder;
    private HashMap<IXmlObject, String> children;
    private LinkedList<ESXmlCollection> collections;
    private Map<IXmlObject, String> childrenNotParsed;

    public<T extends IXmlObject> void registerChild(Property<T> property, String propertyXmlName, ESConstructor<T, Element> constructor)
    {
        IXmlObject o = new IXmlObject()
        {
            @Override
            public Element getXml(String xmlName)
            {
                if (property.getValue() == null)
                {
                    Element ret = new Element(xmlName);
                    ret.setAttribute("isnull", "isnull");
                    return ret;
                }
                else
                    return property.getValue().getXml(xmlName);
            }

            @Override
            public void parseXml(Element data)
            {
                if (data.getAttribute("isnull") == null)
                {
                    T newObj = constructor.createObject(data);
                    property.setValue(newObj);
                }
                else
                    property.setValue(null);
            }

            @Override
            public String getXmlName()
            {
                return propertyXmlName;
            }
        };
        registerChild(o);
    }

    // только для примитивных типов и тех которые перечислены в ParseSettingValue
    public<T> void registerAttribute(Property<T> property, String xmlName)
    {
        ISettingValue iSettingValue = new ISettingValue() {
            @Override
            public String getXmlName()
            {
                return xmlName;
            }

            @Override
            public Object get()
            {
                return property.getValue();
            }

            @Override
            public void set(Object newValue)
            {
                ((Property<Object>) property).setValue(newValue);
            }
        };

        valueHolder.add(iSettingValue);

        if (rootXml != null)
            parseSettingValue(iSettingValue);
    }


    public void registerChild(IXmlObject childXmlObject)
    {
        registerChild(childXmlObject, childXmlObject.getXmlName());
    }

    // регистрируем любой объект ESSettings для загрузки XML и сохранения его XML в текущем объекте настроек
    public void registerChild(IXmlObject childXmlObject, String xmlName)
    {
        if (rootXml == null) // если ещё не прошла инициализация parseXml
        {
            if (debug())
                Log.warning("adding childrenNotParsed and wait for parse: "+xmlName);

            if (childrenNotParsed == null)
                childrenNotParsed = new HashMap<>();

            childrenNotParsed.put(childXmlObject, xmlName);
            return;
        }

        if (debug())
            Log.warning("adding children and parse now: "+xmlName);


        if (children == null)
            children = new HashMap<>();
        
        children.put(childXmlObject, xmlName);

        parseChild(childXmlObject, xmlName);
    }

    private void parseChild(IXmlObject childXmlObject, String xmlName)
    {
        if (xmlName != null)
        {
            Element childXml = rootXml.getChild(xmlName);
            if (childXml != null)
            {
                try
                {
                    childXmlObject.parseXml(childXml);
                }
                catch (Exception e)
                {
                    Log.warning(getXmlName() + ": Error when parse " + xmlName);
                    e.printStackTrace();
                }
            }
            else if (childXmlObject.alertIfNotFound())
                Log.warning("ESSettings: registerChild parse failed. Child not found in parent xml: " + xmlName);
        }
    }

    // если текущий объект имеет родительский объект, то для его сохранения и загрузки требуется уникальное имя
    public abstract String getXmlName();

    // достаём XML всех настроек
    public Element getXml(String xmlName)
    {
        Element xml = new Element(xmlName);

        if (debug())
            Log.warning("Starting getXML for "+getXmlName());

        // для коллекций метод сохранения XMl немного другой.
        if (debug())
            Log.warning(getXmlName()+": getXml() collections");
        if (collections != null)
        {
            for (ESXmlCollection v : collections)
            {
                if (debug())
                    Log.warning(getXmlName() + ": collection '" + v.getXmlName() + "'");
                v.appendXml(xml);
            }
        }

        if (debug())
            Log.warning(getXmlName()+": getXml() valueHolder");
        for (ISettingValue value : valueHolder)
        {
            if (!value.allowSaveMe())
                continue;

            if (debug())
                Log.warning(getXmlName()+": valueHolder '"+value.getXmlName()+"'");

            if (value.getXmlName() != null && value.get() != null)
                xml.setAttribute(value.getXmlName(), value.getForSave().toString());
            else if (debug())
                Log.warning(getXmlName()+": valueHolder NOT SET, because get() == null or getXmlName == null. '"+value.getXmlName()+"'");
        }

        if (debug())
            Log.warning(getXmlName()+": getXml() attributeObjHolder");

        if (attributeObjHolder != null)
        {
            for (SettingAttributeObject v : attributeObjHolder)
            {
                if (debug())
                    Log.warning(getXmlName() + ": attributeObjHolder '" + v.getXmlName() + "'");

                if (v.getXmlName() != null && v.save)
                {
                    String vv = v.toString();
                    if (vv != null)
                        xml.setAttribute(v.getXmlName(), vv);
                }
                else if (debug())
                    Log.warning(getXmlName() + ": attributeObjHolder NOT SET, because getXmlName == null. '" + v.getXmlName() + "'");
            }
        }

        if (debug())
            Log.warning(getXmlName()+": getXml() children");

        Map<IXmlObject, String> combineMap = new FastMap<>();
        if (children != null)
            combineMap.putAll(children);

        if (childrenNotParsed != null)
            combineMap.putAll(childrenNotParsed);

        for (Map.Entry<IXmlObject, String> e : combineMap.entrySet())
        {
            IXmlObject v = e.getKey();
            String objXmlName = e.getValue();

            if (debug())
                Log.warning(getXmlName()+": child '"+objXmlName+"'"+", object: "+e.getKey());

            Element el = null;

            if (objXmlName != null)
            {
                el = v.getXml(objXmlName);

                if (el != null)
                {
                    Parent p = el.getParent();
                    if (p != null)
                    {
                        el.detach();
                    }
                    if (p instanceof Document)
                    {
                        el.detach();
                    }

                    xml.addContent(el);
                }
            }
            else if (debug())
                Log.warning(getXmlName() + ": children NOT SET, because getXmlName == null. '" + objXmlName + "'");

        }

        if (debug())
            Log.warning(getXmlName()+": unparsed children size: "+(childrenNotParsed == null? "null":childrenNotParsed.size()));

        return xml;
    }

    // парсим все настройки
    public void parseXml(Element rootXml)
    {
        if (rootXml == null)
        {
            Log.warning("ROOT XML IS NULL! "+getXmlName()+". Скорее всего указан не правильный elementName в цепочке");
            try
            {
                throw new Exception();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return;
        }

        this.rootXml = rootXml;

        if (debug())
            Log.info(getXmlName()+": Count - valueHolders: " + valueHolder.size() + "," +
                    " attributeObjHolder: " + (attributeObjHolder==null?"null":attributeObjHolder.size()) + "," +
                    (collections==null?" collections:null, ":" collections: " + collections.size() + "," )+
                    " children: " + (children == null ? "null":children.size()));

        // парсим примитивные атрибуты
        if (debug())
            Log.warning(getXmlName()+": Parsing attributeObjHolder");

        if (attributeObjHolder != null)
        {
            for (SettingAttributeObject v : attributeObjHolder)
            {
                if (debug())
                    Log.warning(getXmlName() + ": Parsing " + v.getXmlName() + "...");

                if (v.getXmlName() != null)
                {
                    Attribute a = rootXml.getAttribute(v.getXmlName());
                    if (a != null)
                    {
                        v.parse(a.getValue());
                    }
                }
            }
        }

        if (debug())
            Log.warning(getXmlName()+": Parsing collections");
        if (collections != null)
        {
            for (ESXmlCollection v : collections)
            {
                if (debug())
                    Log.warning(getXmlName() + ": Parsing " + v.getXmlName() + "...");

                v.parseXml(rootXml);
            }
        }

        // парсим примитивные типы
        if (debug())
            Log.warning(getXmlName()+": Parsing valueHolder");
        for (ISettingValue value : valueHolder)
        {
            parseSettingValue(value);
        }

        if (debug())
            Log.warning(getXmlName()+": Checking childrenNotParsed");

        List<IXmlObject> childrenSorted = new LinkedList<>();
        if (childrenNotParsed != null)
        {
            if (children == null)
                children = new HashMap<>();

            children.putAll(childrenNotParsed);
            childrenNotParsed.clear();
        }

        if (children != null)
        {
            childrenSorted.addAll(children.keySet());
            childrenSorted.sort(new Comparator<IXmlObject>()
            {
                @Override
                public int compare(IXmlObject o1, IXmlObject o2)
                {
                    return o1.parsePriority() - o2.parsePriority();
                }
            });
            /*for (Map.Entry<ESXmlObject, String> entry : children.entrySet())
            {
                parseChild(entry.getKey(), entry.getValue());
            } */
            for (IXmlObject key : childrenSorted)
            {
                parseChild(key, children.get(key));
            }
        }


        if (debug())
            Log.warning(getXmlName()+": Parsing done.");
    }

    private Class[] allowedClasses = new Class[]
            {
                    Color.class,
                    File.class,
                    Enum.class,
                    Boolean.class,
                    Integer.class,
                    Double.class,
                    Float.class,
                    Long.class,
                    String.class
            };

    protected <T> boolean allowSaveType(T defaultValue)
    {
        for (Class c : allowedClasses)
        {
            if (defaultValue.getClass() == c)
                return true;
        }
        return false;
    }


    protected void parseSettingValue(ISettingValue value)
    {
        if (debug())
            Log.warning(getXmlName()+": Parsing setting value "+ value.getXmlName()+"...");

        if (value.getXmlName() != null)
        {
            Attribute a = rootXml.getAttribute(value.getXmlName());
            if (a != null && value.get() != null)
            {

                if (debug())
                    Log.warning(getXmlName()+": Parsing setting value (2) "+ value.getXmlName()+"...");

                if (value.get() instanceof Color)
                {
                    if (!a.getValue().isEmpty())
                        value.setAfterLoad(Color.web(a.getValue()));
                }
                else if (value.get() instanceof Enum)
                {
                    Enum en  = (Enum) value.get();
                    Class enumClass = en.getDeclaringClass();
                    value.setAfterLoad(en.valueOf(enumClass, a.getValue()));
                }
                else if (value.get().getClass() == Boolean.class)
                    value.setAfterLoad(Boolean.parseBoolean(a.getValue()));
                else if (value.get().getClass() == Integer.class)
                    value.setAfterLoad(Integer.parseInt(a.getValue()));
                else if (value.get().getClass() == Double.class)
                    value.setAfterLoad(Double.parseDouble(a.getValue()));
                else if (value.get().getClass() == Float.class)
                    value.setAfterLoad(Float.parseFloat(a.getValue()));
                else if (value.get().getClass() == Long.class)
                {
                    if (debug())
                        Log.warning(getXmlName()+": Is Long "+ value.getXmlName()+"...");

                    value.setAfterLoad(Long.parseLong(a.getValue()));
                }
                else if (value.get().getClass() == String.class)
                    value.setAfterLoad((String) a.getValue());
                else if (value.get().getClass() == File.class)
                    value.setAfterLoad(new File(a.getValue()));
                else
                    Log.warning(getXmlName()+": Error in "+getXmlName()+" settings: Setting " + value.getXmlName() + " is not boolean/integer/double/long/String.");

                value.parsed();
            }
        }
    }


    public interface ISettingValue<T> extends ESValue<T>
    {
        String getXmlName();

        default void setAfterLoad(T value)
        {
            set(value);
        }

        default void parsed()
        {

        }

        default boolean allowSaveMe()
        {
            return true; // false for deprecated xmls
        }

        default T getForSave()
        {
            return get();
        }
    }

    //только для примитивных типов
    public class SettingValue<T> extends ESProperty<T> implements ISettingValue<T>
    {
        final String xmlName;
        //public T notSetOnParseIfValueEqualsIt = null;
        Class isEnumClass;

        public SettingValue(T defaultValue, final String xmlName)
        {
            super(defaultValue);
            this.xmlName = xmlName;

            if (defaultValue instanceof Enum)
            {
                isEnumClass = ((Enum) get()).getDeclaringClass();
            }

            valueHolder.add(SettingValue.this);

            if (rootXml != null)
                parseSettingValue(SettingValue.this);
        }

        // только для наследования
        private SettingValue(final String xmlName, T defaultValue, boolean isAttr) // isAttr это затычка
        {
            super(defaultValue);
            this.xmlName = xmlName;
        }

        public void setAfterLoad(T value)
        {
            //if (!value.equals(notSetOnParseIfValueEqualsIt))
                set(value);

        }

        public String getXmlName()
        {
            return xmlName;
        }
    }


    //для любых типов, сохраняемых в атрибут (не составные xml)
    public abstract class SettingAttributeObject<T> extends SettingValue<T>
    {
        public boolean save = true;

        public SettingAttributeObject(T defaultValue, final String xmlName)
        {
            super(xmlName, defaultValue, true);

            if (attributeObjHolder == null)
                attributeObjHolder = new LinkedList<SettingAttributeObject>();

            attributeObjHolder.add(SettingAttributeObject.this);

            if (debug())
            {
                Log.warning("Create attribute object: "+xmlName+", rootXml=null?: "+(rootXml== null));
            }

            if (rootXml != null)
                parseSettingValue(SettingAttributeObject.this);
        }

        public abstract void parse(String fromString);

        public abstract String toString();
    }

    //для старых версий программы (для атрибутов)
    public abstract class OldVersionAttribute<T> extends SettingAttributeObject<T>
    {
        public OldVersionAttribute(String xmlName)
        {
            super(null, xmlName);
            this.save = false;
        }

        public abstract void parse(String fromString);

        public String toString()
        {
            return null;
        }
    }

    // любой объект с ручной реализацией getXml, setXml
    public abstract class SettingObject<T> extends ESProperty<T> implements IXmlObject
    {
        String xmlName;

        public SettingObject(T defaultValue, String xmlName)
        {
            super(defaultValue);
            this.xmlName = xmlName;
            registerChild(this);
        }

        public abstract Element getXml(String name);

        public abstract void parseXml(Element xml);


        @Override
        public String getXmlName()
        {
            return xmlName;
        }
    }


    // сохраняемый ESXmlObject
    public class SettingXmlObject<T extends IXmlObject> extends SettingObject<T>
    {
        ESConstructor<T, Element> constructor;

        public SettingXmlObject(/*@NotNull */T defaultValue, String xmlName)
        {
            super(defaultValue, xmlName);
        }

        public SettingXmlObject(ESConstructor<T, Element> constructor, String xmlName)
        {
            super(null, xmlName);
            this.constructor = constructor;
        }

        // объекты сохраняются через getXml, parseXml
        public Element getXml(String xmlName)
        {
            if (get() == null)     // todo поменял это
                return null;

            return get().getXml(xmlName);
        }

        public void parseXml(Element data)
        {
            if (constructor == null)
                get().parseXml(data);
            else
            {
                set(constructor.createObject(data));
            }
        }
    }

    // для индекса из листа
    public abstract class SettingIndexOf<T> extends SettingAttributeObject<T>
    {
        // класс абстрактный, и мы не можем сразу задать значения, т.к. чаще всего класс создаётся в инициализаторе объекта (не в конструкторе)
        // а на этапе инициализации мы не можем ссылаться на многие объекты, т.к. они ещё не созданы или не загружены
        public SettingIndexOf(T defaultValue, String xmlName)
        {
            super(defaultValue, xmlName);
        }

        public abstract List<T> getEnums();

        @Override
        public void parse(String fromString)
        {
            int index = Integer.parseInt(fromString);
            if (index != -1 && index < getEnums().size())
                set(getEnums().get(index));
        }

        @Override
        public String toString()
        {
            return ""+getEnums().indexOf(get());
        }
    }

    @Deprecated
    public class SettingXMLObservableList<T extends IXmlObject> implements ESXmlCollection
    {
        public String xmlName = "";
        public ESConstructor<T, Element> constructor;
        List<T> list;

        public SettingXMLObservableList(List<T> list, String itemsXmlName, ESConstructor<T, Element> constructor)
        {
            this.list = list;
            this.xmlName = itemsXmlName;
            this.constructor = constructor;

            if (collections == null)
                collections = new LinkedList<>();

            collections.add(this);
        }

        @Override
        public void parseXml(Element rootContainer)
        {
            // new method
            if (debug())
                Log.warning("parse SettingXMLObservableList XML");

            list.clear();
            for (Element e : rootContainer.getChildren(xmlName))
            {
                try
                {
                    if (debug())
                        Log.warning("Creating object "+xmlName);

                    T object = constructor.createObject(e);
                    if (object != null)
                        list.add(object);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void appendXml(Element to)
        {
            for (IXmlObject s : list)
            {
                //to.addContent(s.getXml(s.getXmlName()));
                to.addContent(s.getXml(xmlName));
            }
        }

        public String getXmlName()
        {
            return xmlName;
        }
    }

    @Deprecated
    public class SettingXMLObservableSet<T extends IXmlObject> implements ESXmlCollection
    {
        String xmlName = "";
        ESConstructor<T, Element> constructor;
        Set<T> set;

        public SettingXMLObservableSet(Set<T> set, String itemsXmlName, ESConstructor<T, Element> constructor)
        {
            this.xmlName = itemsXmlName;
            this.constructor = constructor;
            this.set = set;

            if (collections == null)
                collections = new LinkedList<>();

            collections.add(this);
        }

        @Override
        public void parseXml(Element rootContainer)
        {
            // new method
            set.clear();
            for (Element e : rootContainer.getChildren(xmlName))
            {
                try
                {
                    set.add(constructor.createObject(e));
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        }

        @Override
        public void appendXml(Element to)
        {
            for (IXmlObject s : set)
            {
                to.addContent(s.getXml(xmlName));
            }
        }

        public String getXmlName()
        {
            return xmlName;
        }
    }


    public boolean debug()
    {
        return false;
    }


    public void clearXml()
    {
        /**if (rootXml != null)
         {
         rootXml.detach();
         rootXml = null;
         }  **/
    }

    public void detachXml()
    {
        /**if (rootXml != null)
         {
         rootXml.detach();
         }  **/
    }

    public Element getRootXml()
    {
        return rootXml;
    }

    public abstract class SettingForOldVersions<T> extends SettingValue<T>
    {
        public SettingForOldVersions(T defaultVal, String xmlName)
        {
            super(defaultVal, xmlName);
        }

        public boolean allowSaveMe()
        {
            return false;
        }

        @Override
        public abstract void parsed();
    }


}
