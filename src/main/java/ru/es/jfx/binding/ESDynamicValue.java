package ru.es.jfx.binding;

import ru.es.lang.ESValue;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by saniller on 25.07.2016.
 */
public class ESDynamicValue<T> extends ESProperty<T>
{
    ESValue<T> valueSrc;

    public ESDynamicValue(ESValue<T> value, ReadOnlyProperty... getWhenTheyChange)
    {
        super(null);
        this.valueSrc = value;
        set(value.get());

        ChangeListener chl = new ChangeListener()
        {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue)
            {
                setValue(value.get());
            }
        };

        for (ReadOnlyProperty p : getWhenTheyChange)
        {
            p.addListener(chl);
        }
        setValue(value.get());
    }


    // getter не может меняться таким образом
    public void set(T newValue)
    {
        super.set(newValue);

        valueSrc.set(newValue);
    }
}
