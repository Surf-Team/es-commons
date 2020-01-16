package ru.es.audio.device;

import ru.es.lang.ESEventDispatcher;
import ru.es.jfx.ESXmlObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import ru.es.log.Log;

/**
 * Created by saniller on 30.03.2017.
 */
public class Clip extends ESXmlObject
{
    @Override
    public String getXmlName()
    {
        return "Clip";
    }

    public SettingValue<Integer> length = new SettingValue<>(0, "length");
    public SettingValue<Integer> startTick = new SettingValue<>(0, "startTick");
    public SettingValue<Integer> contentOffset = new SettingValue<>(0, "contentOffset");
    public SettingValue<Integer> sampleOffset = new SettingValue<>(0, "sampleOffset");

    public SettingValue<Integer> type = new SettingValue<>(0, "type"); // 0 - midi, 1 - audio

    // где был нарисован клип в последний раз
    public double drawedXStart = Integer.MIN_VALUE;
    public double drawedXEnd = Integer.MIN_VALUE;

    // сохранённые позиции
    public int tickOnPress = 0;
    public int lengthOnPress = 0;
    public int contentOffsetOnPress = 0;
    public int sampleOffsetOnPress = 0;

    // фантомные данные при переносе
    public int phantomStartTick = 0;
    public int phantomLength = 0;
    public int phantomContentOffset = 0;
    public int phantomSampleOffset = 0;

    public boolean isPhantom = false;

    public Object phantomRow;

    public ESEventDispatcher somethingChanged = new ESEventDispatcher();

    public void savePosition()
    {
        tickOnPress = startTick.get();
        lengthOnPress = length.get();
        contentOffsetOnPress = contentOffset.get();
        sampleOffsetOnPress = sampleOffset.get();

        phantomStartTick = startTick.get();
        phantomLength = length.get();
        phantomContentOffset = contentOffset.get();
        phantomSampleOffset = sampleOffset.get();
    }

    public void dropFromFantom()
    {
        startTick.set(phantomStartTick);
        length.set(phantomLength);
        contentOffset.set(phantomContentOffset);
        sampleOffset.set(phantomSampleOffset);
        isPhantom = false;
    }


    public int getLength()
    {
        return length.get();
    }

    public int getStartTick()
    {
        return startTick.get();
    }

    public int getEndTick()
    {
        return getStartTick() + getLength();
    }

    public Clip()
    {
        length.addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                //Log.warning("length changed");
                if (eventLaterOnLenUpdate())
                    ; // не обновляем клипы, которые записываются, иначе слишком частое обновление идёт! записываемые клипы обновляются таймером в клипе: taktPlayed
                    //Log.warning("record enabled");
                else
                    somethingChanged.eventLater();
            }
        });
        contentOffset.addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                Log.warning("Clip: contentOffset changed");
                somethingChanged.eventLater();
            }
        });
        sampleOffset.addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                Log.warning("Clip: sampleOffset changed");
                somethingChanged.eventLater();
            }
        });
        startTick.addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                Log.warning("Clip: startTick changed");
                somethingChanged.eventLater();
            }
        });
    }

    protected boolean eventLaterOnLenUpdate()
    {
        return true;
    }

    public int getMinSize()
    {
        return 1;
    }
    public int getMaxSize()
    {
        return 999999;
    }
}
