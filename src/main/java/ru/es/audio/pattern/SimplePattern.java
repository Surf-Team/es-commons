package ru.es.audio.pattern;

import javolution.util.FastSet;
import ru.es.audio.PatternUtils;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESProperty;
import ru.es.lang.ESConstructor;
import ru.es.jfx.ESXmlObject;
import ru.es.lang.ESEventDispatcher;
import ru.es.math.ESMath;
import ru.es.models.ESArrayList;
import ru.es.thread.RunnableImpl;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javolution.util.FastTable;
import org.jdom2.Element;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Простая партия
 */
public class SimplePattern extends ESXmlObject
{
    public PianoRollState pianoRollState;

    // минимальная единица времени - 1 тик
    // 6 тиков = 1 такт
    // 16 шагов = 1 бар

    // ноты в текущей партии
    public ObservableSet<SQNote> notes = FXCollections.observableSet(new FastSet<>());
    private SettingXMLObservableSet<SQNote> notesLoading = new SettingXMLObservableSet<SQNote>(notes, "SQNote", new ESConstructor<SQNote, Element>()
    {
        @Override
        public SQNote createObject(Element... fromThis)
        {
            return new SQNote(fromThis[0]);
        }
    });


    public ESEventDispatcher noteVeloLengthToneStartSylenthDispatcher = new ESEventDispatcher();
    public ESEventDispatcher somethingChangedDelayed = new ESEventDispatcher();
    {
        somethingChangedDelayed.isSelfRunLater = true;
        noteVeloLengthToneStartSylenthDispatcher.addOnEvent(somethingChangedDelayed);
    }


    public boolean isEmpty()
    {
        return isEmptyStat.get();
    }


    @Override
    public void parseXml(Element rootXml)
    {
        super.parseXml(rootXml);
        somethingChangedDelayed.event();
    }


    public final ESArrayList<SQNote> notesListStat = new ESArrayList<>();

    public ESProperty<Boolean> isEmptyStat = new ESProperty<>(true);
    {

        notes.addListener(new SetChangeListener<SQNote>()
        {
            @Override
            public void onChanged(Change<? extends SQNote> change)
            {
                SQNote added = change.getElementAdded();
                SQNote removed = change.getElementRemoved();

                notesListStat.add(added);
                notesListStat.remove(removed);

                if (added != null)
                    added.somethingChanged.onEvent = noteVeloLengthToneStartSylenthDispatcher;
                if (removed != null)
                    removed.somethingChanged.onEvent = null;

                somethingChangedDelayed.event();
            }
        });


        somethingChangedDelayed.addOnEvent(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                for (SQNote n : notes)
                {
                    if (n == null)
                        continue;

                    if (n.isSylenth())
                        continue;

                    if (n.getStartTick() >= 0 && n.getStartTick() < getLengthInTick())
                    {
                        isEmptyStat.set(false);
                        return;
                    }
                }
                isEmptyStat.set(true);
            }
        });
    }



    public static SimpleIntegerProperty quantize = new SimpleIntegerProperty(PatternUtils.TICKS_IN_STEP); // квантизация (округление при рисовании нот)

    // количество линий в текущей партии (1 линия = 16 тактов = 16*6 тиков
    public SettingValue<Integer> barsCount = new SettingValue<>(1, "linesCount");

    public SettingValue<Integer> loopEnd = new SettingValue<>(PatternUtils.TICKS_IN_BAR, "patternLengthRepeat");
    {
        barsCount.addListener(new ESChangeListener<Integer>(true) {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                int ticksOld = oldValue * PatternUtils.TICKS_IN_BAR; // == getLengthInTick()
                int ticksNew = newValue * PatternUtils.TICKS_IN_BAR; // == getLengthInTick()

                if (ticksOld == loopEnd.get() || ticksNew < loopEnd.get())
                {
                    loopEnd.set(ticksNew);
                }
            }
        });
        loopEnd.addListener(noteVeloLengthToneStartSylenthDispatcher);
    }



    public int tickInPattern = 0;

    @Override
    public String getXmlName()
    {
        return "Pattern";
    }


    public Set<SQNote> getNotes()
    {
        return notes;
    }

    public int getBarsCount()
    {
        return barsCount.get();
    }

    public void setBarsCount(int barsCount)
    {
        this.barsCount.set(barsCount);
    }

    public static String convertTicksToString(int ticks)
    {
        int bars = ticks / PatternUtils.TICKS_IN_BAR;
        int steps = 0;
        ticks -= bars * PatternUtils.TICKS_IN_BAR;
        if (ticks > 0)
            steps = ticks / PatternUtils.TICKS_IN_STEP;

        ticks -= steps * PatternUtils.TICKS_IN_STEP;

        String ret = "";
        if (bars > 0)
            ret +=bars+" бар ";
        if (steps > 0)
            ret +=steps+" шагов ";
        if (ticks > 0)
            ret +=ticks+" тиков ";

        return ret;
    }

    public SimplePattern()
    {
        loopEnd.addListener(new ESChangeListener<Integer>(true) {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                somethingChangedDelayed.event();
            }
        });
    }

    public void removeAllNotes()
    {
        notes.clear();
    }

    public SQNote addNoteToPoint(int startPoint, int velo, int size, Tone t, boolean infinity)
    {
        SQNote add = new SQNote(startPoint, velo, size, false, t);
        add.setInfinity(infinity);
        notes.add(add);
        return add;
    }


    // get notes in zone
    public List<SQNote> getNotesFromPoint(long fromTick, long toTickExceptItself, boolean checkNotesSize, boolean onlyNonDublicated)
    {
        List<SQNote> ret = new FastTable<SQNote>();

        if (!checkNotesSize)
        {
            for (SQNote n : notes)
            {
                if (!ret.contains(n) && n.getStartTick() >= fromTick && n.getStartTick() < toTickExceptItself)
                    ret.add(n);
            }
        }
        else
        {
            for (SQNote n : notes)
            {
                if (n.getStartTick() >= fromTick && n.getStartTick() <= toTickExceptItself)
                    ret.add(n);
                else if (n.getStartTick() +(n.getLength()-1) >= fromTick && n.getStartTick()+(n.getLength()-1) <= toTickExceptItself)
                    ret.add(n);
                else if (n.getStartTick() < fromTick && n.getStartTick()+(n.getLength()-1) >= toTickExceptItself)
                    ret.add(n);
            }
        }

        if (onlyNonDublicated)
        {
            List<SQNote> dublicatedNotes = new FastTable<SQNote>();
            for (SQNote n1 : ret)
            {
                for (SQNote n2 : ret)
                {
                    if (n1 == n2)
                        continue;

                    if (n1.equals(n2))
                        if (!dublicatedNotes.contains(n1) && !dublicatedNotes.contains(n2))
                            dublicatedNotes.add(n1);
                }
            }
            ret.removeAll(dublicatedNotes);
        }

        return ret;
    }

    public List<SQNote> getNotesFromPoint(int fromTick, int toTickExceptItself, int note, boolean checkNotesSize, boolean onlyNonDublicated)
    {
        if (note == -1)
            return getNotesFromPoint(fromTick, toTickExceptItself, checkNotesSize, onlyNonDublicated);

        List<SQNote> ret = new FastTable<SQNote>();

        if (!checkNotesSize)
        {
            for (SQNote n : notes)
            {
                if (!ret.contains(n) && n.getStartTick() >= fromTick && n.getStartTick() < toTickExceptItself && note == n.getTone().getIndex())
                    ret.add(n);
            }
        }
        else
        {
            for (SQNote n : notes)
            {
                if (note != n.getTone().getIndex())
                    continue;

                if (n.getStartTick() >= fromTick && n.getStartTick() <= toTickExceptItself)
                    ret.add(n);
                else if (n.getStartTick() +(n.getLength()-1) >= fromTick && n.getStartTick()+(n.getLength()-1) <= toTickExceptItself)
                    ret.add(n);
                else if (n.getStartTick() < fromTick && n.getStartTick()+(n.getLength()-1) >= toTickExceptItself)
                    ret.add(n);
            }
        }

        if (onlyNonDublicated)
        {
            List<SQNote> dublicatedNotes = new FastTable<SQNote>();
            for (SQNote n1 : ret)
            {
                for (SQNote n2 : ret)
                {
                    if (n1 == n2)
                        continue;

                    if (n1.equals(n2))
                        if (!dublicatedNotes.contains(n1) && !dublicatedNotes.contains(n2))
                            dublicatedNotes.add(n1);
                }
            }
            ret.removeAll(dublicatedNotes);
        }

        return ret;
    }

    public FastTable<SQNote> getNotesFromZone(int tick1, int tick2, int note1, int note2, boolean onlyNonDublicated)
    {
        FastTable<SQNote> ret = new FastTable<SQNote>();

        int tickMin = ESMath.min(tick1, tick2);
        int tickMax = ESMath.max(tick1, tick2);
        int noteMin = ESMath.min(note1, note2);
        int noteMax = ESMath.max(note1, note2);

        for (int i = noteMin; i <= noteMax; i++)
        {
            ret.addAll(getNotesFromPoint(tickMin, tickMax, i, true, onlyNonDublicated));
        }

        return ret;
    }

    public void removeAllNotesFromPoint(long fromTick, long toTickExceptItself, boolean checkNotesSize)
    {
        notes.removeAll(getNotesFromPoint(fromTick, toTickExceptItself, checkNotesSize, false));
    }

    public void removeNoteFromPoint(int fromTick, int toTickExceptItself, int note, boolean checkNotesSize)
    {
        notes.removeAll(getNotesFromPoint(fromTick, toTickExceptItself, note, checkNotesSize, false));
    }

    public void removeNotes(Collection<SQNote> remove)
    {
        notes.removeAll(remove);
    }

    public List<SQNote> findNotesToSelectByPoint(int startPoint, int endPoint, int startNote, int endNote)
    {
        List<SQNote> newSelectedNotesList = new FastTable<>();
        for (SQNote note : getNotesFromZone(startPoint, endPoint, startNote, endNote, true))
        {
            newSelectedNotesList.add(note);
            note.savePosition();
        }
        return newSelectedNotesList;
    }


    public int getLengthInTick()
    {
        return barsCount.get() * PatternUtils.TICKS_IN_BAR;
    }

    public Collection<SQNote> getNotesReadOnly()
    {
        return notesListStat;
    }
}
