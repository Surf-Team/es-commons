package ru.es.audio.midi;


import ru.es.jfx.xml.IXmlObject;
import ru.es.log.Log;
import ru.es.util.StringUtils;
import javolution.util.FastTable;
import org.jdom2.Attribute;
import org.jdom2.Element;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.List;
import java.util.StringTokenizer;

public class MidiParameterKey implements IXmlObject
{
    // ########### STATIC ##############
    public static final int TYPE_CONTROLCHANGE = 0;
    public static final int TYPE_POLYPRESSCURE = 1;
    public static final int TYPE_SYSEX = 2;
    public static final int TYPE_PITCHBAND = 3;
    public static final int TYPE_INTERNAL = 4;

    public static final int pageInSysexPacket = 6;
    public static final int channelInSysexPacket = 7;
    public static final int controlInSysexPacket = 8;
    public static final int valueInSysexPacket = 9;


    // ############# NO STATIC ##############
    public byte[] customSysexPacket; // для особых пакетов
    public int sysexPage = -1;
    public int channel = -1;

    public int type;
    public int control;

    public MidiParameterKey(int type, int control)
    {
        this.type = type;
        this.control = control;
    }

    public MidiParameterKey(int control)
    {
        this.type = TYPE_CONTROLCHANGE;
        this.control = control;
    }

    public MidiParameterKey(int type, int sysexPage, int control)
    {
        this(type, control);
        this.sysexPage = sysexPage;
    }

    public MidiParameterKey(MidiMessage message)
    {
        if (((message.getStatus() & 0xF0) == ShortMessage.CONTROL_CHANGE) || ((message.getStatus() & 0xF0) == ShortMessage.POLY_PRESSURE))
        {
            channel = message.getStatus() & 0x0F;
            control = message.getMessage()[1];
            type = TYPE_CONTROLCHANGE;
            if  ((message.getStatus() & 0xF0) == ShortMessage.POLY_PRESSURE)
                type = TYPE_POLYPRESSCURE;
        }
        else if (message.getStatus() == 0xF0)
        {
            type = TYPE_SYSEX;
            channel = message.getMessage()[channelInSysexPacket];
            this.sysexPage = message.getMessage()[pageInSysexPacket];
            this.control = message.getMessage()[controlInSysexPacket];
            Log.warning("Sysex received: "+StringUtils.byteToString(message.getMessage(), " "));

            customSysexPacket = message.getMessage();
        }
    }

    // sysex initial
    public MidiParameterKey(byte[] packet)
    {
        this.type = TYPE_SYSEX;
        this.sysexPage = packet[pageInSysexPacket];
        this.control = packet[controlInSysexPacket];
    }

    public MidiParameterKey(Element xml)
    {
        parseXml(xml);
    }

    public MidiParameterKey(MidiParameterKey key)
    {
        parseXml(key.getXml("copy"));
    }

    @Override
    public String getXmlName()
    {
        return "MidiParameter";
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (!(obj instanceof MidiParameterKey))
            return false;

        MidiParameterKey that = (MidiParameterKey) obj;

        //if (customSysexPacket != null) //sysex сразу разбиваются на control\channel\page\value, поэтому такое не нужно
            //return Arrays.equals(customSysexPacket, that.customSysexPacket);
        if (control != that.control)
            return false;
        if (sysexPage != that.sysexPage)
            return false;
        if (type != that.type)
            return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = type;
        result += control * 10;
        result += sysexPage * 10000;
        return result;
    }

    public int getType()
    {
        return type;
    }

    public String getTypeString()
    {
        if (getType() == TYPE_CONTROLCHANGE)
            return "CC";
        else if (getType() == TYPE_INTERNAL)
            return "VST";
        else if (getType() == TYPE_POLYPRESSCURE)
            return "PP";
        else if (getType() == TYPE_SYSEX)
            return "SYS";
        else if (getType() == TYPE_PITCHBAND)
            return "PITCH";

        return "UNK";
    }

    public static int getTypeInt(String type)
    {
        if (type.toUpperCase().equals("CC"))
            return TYPE_CONTROLCHANGE;
        if (type.toUpperCase().equals("PP"))
            return TYPE_POLYPRESSCURE;
        if (type.toUpperCase().equals("SYS"))
            return TYPE_SYSEX;
        if (type.toUpperCase().equals("VST"))
            return TYPE_INTERNAL;

        return -1;
    }


    public String toString()
    {
        if (getType() != TYPE_SYSEX)
            return getTypeString() + " " + control;
        else
            return getTypeString() + " " + sysexPage + " " + control;
    }


    public int getControl()
    {
        return control;
    }


    public Element getXml(String xmlName, boolean customSysexToo)
    {
        Element ret = new Element(xmlName);

        ret.setAttribute(new Attribute("short", getType()+","+getControl()+","+sysexPage));
        if (customSysexToo && customSysexPacket != null && customSysexPacket.length != 0)
        {
            ret.setAttribute(new Attribute("receivedPacket", StringUtils.byteToString(customSysexPacket, " ")));
        }

        return ret;
    }

    @Override
    public Element getXml(String xmlName)
    {
        return getXml(xmlName, true);
    }

    // при изменении менять MidiParameter.parseXml. Не сделал супер метод, т.к. используется StringTokenizer для поиска значения в случае с short
    public void parseXml(Element data)
    {
        try
        {
            if (data.getAttribute("short") != null)
            {
                String v = data.getAttribute("short").getValue();
                StringTokenizer t = new StringTokenizer(v, ",");

                type = Integer.parseInt(t.nextToken());
                control = Integer.parseInt(t.nextToken());
                sysexPage = Integer.parseInt(t.nextToken());
            }
            else
            {
                if (data.getAttribute("type") != null)
                    type = Integer.parseInt(data.getAttribute("type").getValue());
                if (data.getAttribute("control") != null)
                    control = Integer.parseInt(data.getAttribute("control").getValue());
                if (data.getAttribute("sysexPage") != null)
                    sysexPage = Integer.parseInt(data.getAttribute("sysexPage").getValue());
            }

            if (data.getAttribute("receivedPacket") != null)
            {
                customSysexPacket = StringUtils.stringToByte(data.getAttribute("receivedPacket").getValue(), " ");
            }
        }
        catch (Exception e)
        {
            Log.warning("Can not load midi parameter.");
            e.printStackTrace();
        }
    }

    public static List<MidiParameterKey> parseKeysList(String text)
    {
        StringTokenizer t = new StringTokenizer(text, ";");
        List<MidiParameterKey> ret = new FastTable<>();
        while (t.hasMoreTokens())
        {
            String param = t.nextToken().trim();
            if (!param.isEmpty())
            {
                StringTokenizer st = new StringTokenizer(param, ",");

                int type = Integer.parseInt(st.nextToken());
                int control = Integer.parseInt(st.nextToken());
                int sysexPage = Integer.parseInt(st.nextToken());

                MidiParameterKey p = new MidiParameterKey(type, sysexPage, control);
                ret.add(p);
            }
        }
        return ret;
    }
}

