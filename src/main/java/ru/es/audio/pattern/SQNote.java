package ru.es.audio.pattern;

import ru.es.lang.ESSingleEventDispatcher;
import ru.es.log.Log;
import ru.es.jfx.xml.IXmlObject;
import ru.es.math.ESMath;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Attribute;
import org.jdom2.Element;

/**
 * Нота в парти
 */
public class SQNote implements IXmlObject, Cloneable
{
    public static SimpleIntegerProperty baseVelo = new SimpleIntegerProperty(100);

    private long startTick;
    private int velo;
    private int length;
    private boolean sylenth = false;
    public Tone tone;

    public ESSingleEventDispatcher somethingChanged = new ESSingleEventDispatcher();

    public Element getXml(String xmlName)
    {
        Element ret = new Element(xmlName);

        //ret.addContent(tone.getXml("Tone"));
        //ret.setAttribute("sylenth", "" + sylenth);
        //ret.setAttribute("velo", "" + velo);
        //ret.setAttribute("startTick", "" + startTick);
        //ret.setAttribute("len", "" + length);

        ret.setAttribute("v1", sylenth+","+velo+","+startTick+","+length+","
            +tone.getType().ordinal()+","+tone.getIndex()+","+tone.getOctave());

        return ret;
    }

    public void parseXml(Element data)
    {
        try
        {
            Element toneElement = data.getChild("Tone");
            if (toneElement != null)
                tone = new Tone(toneElement);

            Attribute a;

            a = data.getAttribute("v1");
            if (a != null)
            {
                String[] split = a.getValue().split(",");
                sylenth = Boolean.parseBoolean(split[0]);
                velo = Integer.parseInt(split[1]);
                startTick = Integer.parseInt(split[2]);
                length = Integer.parseInt(split[3]);

                tone = new Tone(Tone.PointType.values()[Integer.parseInt(split[4])], // toneType
                        Integer.parseInt(split[5]), // index
                        Integer.parseInt(split[6])); // octave
            }
            else
            {
                a = data.getAttribute("sylenth");
                if (a != null)
                    sylenth = a.getBooleanValue();

                a = data.getAttribute("velo");
                if (a != null)
                    velo = a.getIntValue();

                a = data.getAttribute("len");
                if (a != null)
                    length = a.getIntValue();

                a = data.getAttribute("startTick");
                if (a != null)
                    startTick = a.getLongValue();
            }
        }
        catch (Exception e)
        {
            Log.warning("Can not load pattern options.");
            e.printStackTrace();
        }
    }



    /**private OldVersionAttribute<Integer> startTickV1 = new OldVersionAttribute<Integer>("startPoint") {
        @Override
        public void parse(String fromString)
        {
            startTIck.set(Long.parseLong(fromString)*2);
        }
    };
    private OldVersionAttribute<Integer> lengthV1 = new OldVersionAttribute<Integer>("length") {
        @Override
        public void parse(String fromString)
        {
            length.set(Integer.parseInt(fromString)*2);
        }
    }; **/

    public SQNote(long startTick, int velo, int length, boolean sylenth, Tone tone)
    {
        this.startTick = startTick;
        this.velo = velo;
        this.length = length;
        this.sylenth = sylenth;
        this.tone = tone;
        savePosition();
    }

    public SQNote(Element e)
    {
        parseXml(e);
        savePosition();
    }

    public boolean equals(SQNote otherNote)
    {
        if (startTick != otherNote.getStartTick())
            return false;
        if (velo != otherNote.getVelo())
            return false;
        if (length != otherNote.getLength())
            return false;
        if (sylenth != otherNote.sylenth)
            return false;
        if (!tone.equals(otherNote.tone))
            return false;

        return true;
    }

    public long getStartTick()
    {
        return startTick;
    }

    public long getEndTick()
    {
        return startTick + length;
    }

    public void setStartTick(long startTick)
    {
        if (startTick == this.startTick)
            return;

        this.startTick = startTick;
        somethingChanged.event();
    }

    public int getLength()
    {
        return length;
    }

    public void setLength(int length)
    {
        if (length == this.length)
            return;

        this.length = length;
        somethingChanged.event();
    }

    public int getVelo()
    {
        return velo;
    }

    public void setVelo(int velo)
    {
        if (velo == this.velo)
            return;

        if (velo > 127)
            this.velo = 127;
        else if (velo < 0)
            this.velo = 0;
        else
            this.velo = velo;

        somethingChanged.event();
    }

    @Override
    public String getXmlName()
    {
        return "SQNote";
    }

    public static int getBaseNote()
    {
        return 60;
    }

    public int preClickedIndex;
    public int preClickedLength;
    public int preClickedTick;
    public int preClickedVelo;

    public void savePosition()
    {
        preClickedIndex = tone.getIndex();
        preClickedTick = (int) getStartTick();
        preClickedLength = getLength();
        preClickedVelo = getVelo();
    }

    public Tone getTone()
    {
        return tone;
    }

    public void setTone(Tone t)
    {
        tone = t;
        somethingChanged.event();
    }

    public boolean isInfinity()
    {
        return false;
    }

    public void setInfinity(boolean b)
    {

    }

    public boolean isSylenth()
    {
        return sylenth;
    }

    public void setSylenth(boolean sylenth)
    {
        if (this.sylenth == sylenth)
            return;

        this.sylenth = sylenth;
        somethingChanged.event();
    }

    @Override
    public SQNote clone()
    {
        return new SQNote(getXml("T"));
    }

    public void setEndTick(long newEndTick)
    {
        long newLength = newEndTick - getStartTick();
        setLength((int) ESMath.max(1, newLength));
    }
}
