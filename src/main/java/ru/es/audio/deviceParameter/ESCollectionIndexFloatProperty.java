package ru.es.audio.deviceParameter;

import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

/**
 * Created by saniller on 22.01.2017.
 */
public class ESCollectionIndexFloatProperty<T> extends DeviceValueProperty
{
    public final ESProperty<T> selectedValue;
    public final ObservableList<T> collection;

    public ESCollectionIndexFloatProperty(ESProperty<T> selectedValue, ObservableList<T> collection)
    {
        super(0.0f);
        this.collection = collection;
        this.selectedValue = selectedValue;

        selectedValue.addListener(new ESChangeListener<T>(true)
        {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                float newVal = collection.indexOf(newValue);
                if (value() != newVal)
                {
                    set(newVal);
                }

            }
        });
        addListener(new ChangeListener<Float>()
        {
            @Override
            public void changed(ObservableValue<? extends Float> observable, Float oldValue, Float newValue)
            {
                T newOsc = collection.get(newValue.intValue());

                if (selectedValue.get() != newOsc)
                {
                    selectedValue.set(newOsc);
                }
            }
        });
    }
}
