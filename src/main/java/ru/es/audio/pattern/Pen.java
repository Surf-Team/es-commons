package ru.es.audio.pattern;

import javolution.util.FastSet;
import javafx.collections.FXCollections;
import ru.es.audio.PatternUtils;
import ru.es.jfx.xml.objects.XmlCollectionOfXmlitems;
import ru.es.thread.ESThreadPoolManager;
import ru.es.thread.RunnableImpl;
import javafx.collections.ObservableSet;
import javolution.util.FastTable;
import ru.es.lang.ESConstructor;
import ru.es.jfx.ESXmlObject;
import org.jdom2.Element;

import java.util.List;
import java.util.Set;

/**
 * Created by saniller on 01.11.2016.
 */
public class Pen extends ESXmlObject
{
    public final ObservableSet<Tone> notes = FXCollections.observableSet(new FastSet<>());

    private XmlCollectionOfXmlitems<Tone> notesLoad = new XmlCollectionOfXmlitems<Tone>(notes, "Tone","Tone", new ESConstructor<Tone, Element>()
    {
        @Override
        public Tone createObject(Element... fromThis)
        {
            return new Tone(fromThis[0]);
        }
    });

    public SettingValue<Integer> velo = new SettingValue<Integer>(100, "velo");
    public SettingValue<Integer> selectedOctave = new SettingValue<>(4, "selectedOctave");
    public SettingValue<Integer> lastSize = new SettingValue<>(PatternUtils.TICKS_IN_STEP, "size");
    public SettingValue<Boolean> infinity = new SettingValue<>(false, "infinity");
    public SettingValue<Integer> recordFixedNoteLength = new SettingValue<Integer>(0, "recordFixedNoteLength");

    public SettingAttributeObject<Tone.PointType> watchType = new SettingAttributeObject<Tone.PointType>(Tone.PointType.Note, "drawType")
    {
        @Override
        public void parse(String fromString)
        {
            watchType.set(Tone.PointType.values()[Integer.parseInt(fromString)]);
        }

        @Override
        public String toString()
        {
            return ""+get().ordinal();
        }
    };

    public final INoteReceiver noteReceiver;

    public Pen(INoteReceiver noteReceiver)
    {
        super(); // parse xml
        this.noteReceiver = noteReceiver;
        setOneNote(new Tone(SQNote.getBaseNote()));
        if (notes.size() == 0)
            setOneNote(new Tone(SQNote.getBaseNote()));

        registerChild(notesLoad);
    }

    @Override
    public String getXmlName()
    {
        return "Pen";
    }

    public Set<Tone> getTones()
    {
        return notes;
    }

    public Tone getFirstTone()
    {
        for (Tone t : getTones())
        {
            return t;
        }
        return null;
    }


    public int getVelo()
    {
        return velo.get();
    }

    public void setVelo(int velo)
    {
        this.velo.set(velo);
    }

    public void setOneNote(Tone note)
    {
        notes.clear();
        notes.add(note);
    }

    public void addNote(Tone note)
    {
        notes.add(note);
    }

    public List<Tone> playMe(final boolean start, boolean addToAccordHistory)
    {
        int t = 0;

        List<Tone> played = new FastTable<>();
        for (Tone tone : getTones())
        {
            noteReceiver.noteReceived(tone, start, getVelo(), false, addToAccordHistory, 0);
            t++;
            played.add(tone);
            if (t > 5)
                break;
        }

        return played;
    }

    public List<Tone> playMe(final boolean start, boolean addToAccordHistory, boolean updatePen)
    {
        int t = 0;

        List<Tone> played = new FastTable<>();
        for (Tone tone : getTones())
        {
            noteReceiver.noteReceived(tone, start, getVelo(), updatePen, addToAccordHistory, 0);
            t++;
            played.add(tone);
            if (t > 5)
                break;
        }

        return played;
    }

    public void simplePlay(int note)
    {
        noteReceiver.noteReceived(new Tone(note), true, getVelo(), false, false, 0);
        ESThreadPoolManager.getInstance().scheduleGeneral(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                noteReceiver.noteReceived(new Tone(note), false, getVelo(), false, false, 0);
            }
        }, 400);
    }

    public void playMe(List<Tone> tones, final boolean start, boolean addToAccordHistory)
    {
        int t = 0;

        List<Tone> played = new FastTable<>();
        for (Tone tone : tones)
        {
            noteReceiver.noteReceived(tone, start, getVelo(), false, addToAccordHistory, 0);
            t++;
            played.add(tone);
            if (t > 5)
                break;
        }
    }

    public void stopNotes(List<Tone> notes, boolean addToAccordHistory)
    {
        for (Tone tone : notes)
        {
            noteReceiver.noteReceived(tone, false, getVelo(), false, addToAccordHistory, 0);
        }
    }
}
