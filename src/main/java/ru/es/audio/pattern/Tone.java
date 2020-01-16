package ru.es.audio.pattern;

import ru.es.jfx.xml.IXmlObject;
import ru.es.util.Words;
import org.jdom2.Attribute;
import org.jdom2.Element;

import java.util.Collection;
import java.util.List;

/**
 * Created by saniller on 19.01.2016.
 */
public class Tone implements IXmlObject
{
    public static enum PointType
    {
        Note,
        Arp,
        Pressed,
        Gamma
    }

    private int index;
    private int octave;
    private PointType type;

    // запрещено менять значения внутри. Используется как структура
    public Tone()
    {
        this(0);
        this.type = PointType.Arp;
    }

    public Tone(int note)
    {
        this.index = note;
        this.type = PointType.Note;
    }

    public Tone(Element e)
    {
        parseXml(e);
    }

    public Tone(PointType type, int index, int octave)
    {
        this.index = index;
        this.type = type;
        this.octave = octave;
    }

    public int getOctave()
    {
        return octave;
    }

    public PointType getType()
    {
        return type;
    }

    public int getIndex()
    {
        return index;
    }

    @Override
    public boolean equals(Object t2)
    {
        if (!(t2 instanceof Tone))
            return false;
        Tone tt2 = (Tone) t2;

        if (this.getType() != tt2.getType())
            return false;

        if (getType() == PointType.Arp) // арпы сразу равны
            return true;
        if (getType() == PointType.Note && getIndex() == tt2.getIndex())
            return true;
        else if (this.getOctave() == tt2.getOctave() && this.getIndex() == tt2.getIndex())
            return true;

        return false;
    }

    @Override
    public int hashCode()
    {
        if (getType() == PointType.Arp)
            return 0;
        else if (getType() == PointType.Note)
            return 10 + getIndex();
        else
            return getType().ordinal()*100000+ getOctave() *1000+ getIndex() *10;
    }

    @Override
    public String getXmlName()
    {
        return "Tone";
    }

    @Override
    public Element getXml(String xmlName)
    {
        Element ret = new Element(xmlName);// super.getXml(xmlName);
        // old type
        //ret.setAttribute("type", "" + getType().ordinal());
        //ret.setAttribute("octave", "" + octave);
        //ret.setAttribute("index", "" + index);

        ret.setAttribute("v1", getType().ordinal()+","+octave+","+index);

        return ret;
    }

    @Override
    public void parseXml(Element data)
    {
        //super.parseXml(data);
        try
        {
            if (data.getAttribute("type") != null)
            {
                Attribute a = data.getAttribute("type");
                if (a != null)
                    this.type = PointType.values()[a.getIntValue()];

                Attribute a2 = data.getAttribute("octave");
                if (a2 != null)
                    octave = a2.getIntValue();

                Attribute a3 = data.getAttribute("index");
                if (a3 != null)
                    index = a3.getIntValue();
            }
            else
            {
                String[] v1 = data.getAttributeValue("v1").split(",");
                this.type = PointType.values()[Integer.parseInt(v1[0])];
                this.octave = Integer.parseInt(v1[1]);
                this.index = Integer.parseInt(v1[2]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String toString()
    {
        if (getType() == PointType.Note)
            return Words.getNoteName(getIndex(), true);
        else if (getType() == PointType.Arp)
            return "ARP";
        else if (getType() == PointType.Pressed)
            return ""+ getOctave() +"PR"+ getIndex();
        else if (getType() == PointType.Gamma)
            return ""+ getOctave() +"G"+ getIndex();

        return "UNK";
    }

    public int getNoteFromActualNotes(List<Integer> notes, Collection<Integer> allTonikaNotes)
    {
        if (getType() == PointType.Note)
            return getIndex();
        if (getType() == PointType.Pressed)
            return notes.get(getIndex() % notes.size()) + (getOctave() *12);
        else
        {
            int startNote = notes.get(0) + (getOctave() *12);
            int indexNow = -1;
            for (int i : allTonikaNotes)
            {
                if (i >= startNote)
                    indexNow++;

                if (indexNow == getIndex())
                    return i;
            }
        }
        //todo arp
        return 62;
    }

    public static Tone arp = new Tone();
}
