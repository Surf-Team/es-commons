package ru.es.jfx.binding;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.lang.ESSetter;
import ru.es.lang.ESValue;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by saniller on 06.07.2016.
 */
public class ESProperty<T> extends SimpleObjectProperty<T> implements ESValue<T>
{
    public static int instances = 0;

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public synchronized void addListener(ChangeListener<? super T> listener)
    {
        super.addListener(listener);
        if (listener instanceof ESChangeListener)
        {
            if (((ESChangeListener)listener).isUpdateOnInit())
                listener.changed(this, getValue(), getValue());
        }
    }

    public ESProperty()
    {
        instances++;
    }

    public ESProperty(T defaultVal)
    {
        super(defaultVal);
    }


    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void set(T newValue)
    {
        if (newValue != get())
        {
            if (get() instanceof Number && get().equals(newValue))
                return;

            //Log.warning("Setted new value: " + newValue);

            
            super.set(newValue);
        }
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void setByUser(T newValue)
    {
        set(newValue);
        if (onUserSet != null)
        {
            for (ESSetter<T> userSet : onUserSet)
            {
                userSet.set(newValue);
            }
        }
    }

    public void eventByUser()
    {
        if (onUserSet != null)
        {
            for (ESSetter<T> userSet : onUserSet)
            {
                userSet.set(get());
            }
        }
    }

    private Set<ESSetter<T>> onUserSet;

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addOnSetByUser(ESSetter<T> s)
    {
        if (onUserSet == null)
            onUserSet = new HashSet<>();

         onUserSet.add(s);
    }
}
