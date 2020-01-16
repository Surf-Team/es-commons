package ru.es.jfx.binding;

import ru.es.lang.ESGetter;
import ru.es.thread.RunnableImpl;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;


public class ESDynamicProperty<T> extends ESProperty<T>
{
    // самообновляющийся ярлык Property
    // функционирует без каких либо ограничений

    // проперти, которая является клоном текущей проперти в данный момент.
    Property<T> activeProperty;

    public boolean blockNotifyIfNull = false; // запрет обновления листенеров, если установлено значение null.
    // Помогает избежать кучи проблем, когда параметр удалён, или ещё не создан

    // листенер для чтения значения эктив проперти
    // добавляется в листенеры activeProperty, и затем убирается в случае смены activeProperty
    ESChangeListener<T> activePropertyListener = new ESChangeListener<T>(true)
    {
        @Override
        public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
        {
            set(newValue);
        }
    };

    // ярлык (activeProperty) берётся из getter.get() в случае смены значений любых Property, перечисленных в getWhenTheyChange
    public ESDynamicProperty(ESGetter<Property<T>> getter, Property... getWhenTheyChange)
    {
        this(getter, null, getWhenTheyChange);
    }

    public ESDynamicProperty(T defaultVal, ESGetter<Property<T>> getter, Property... getWhenTheyChange)
    {
        this(defaultVal, getter, null, getWhenTheyChange);
    }

    ESGetter<Property<T>> getter;

    RunnableImpl updateProperty = new RunnableImpl()
    {
        @Override
        public void runImpl() throws Exception
        {
            updateActiveProperty();
        }
    };

    public ESDynamicProperty(T defaultValue, ESGetter<Property<T>> getter, ESChangeListener<T> onSet, Property... getWhenTheyChange)
    {
        super(defaultValue);

        this.getter = getter;
        activeProperty = getter.get();
        activePropertyChanged(null);

        for (Property p : getWhenTheyChange)
        {
            addCheckingProperty(p, true);
        }
        if (onSet != null)
            addListener(onSet);
    }

    public ESDynamicProperty(ESGetter<Property<T>> getter, ESChangeListener<T> onSet, Property... getWhenTheyChange)
    {
        this(getter.get().getValue(), getter, onSet, getWhenTheyChange);
    }

    public void addCheckingProperty(Property p, boolean update)
    {
        p.addListener(new ChangeListener()
        {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue)
            {
                runUpdate(updateProperty);
            }
        });

        if (update)
            runUpdate(updateProperty);
    }

    public void runUpdate(RunnableImpl updateProperty)
    {
        updateProperty.run();
    }

    void updateActiveProperty()
    {
        Property newProp = getter.get();

        if (activeProperty != newProp)
        {
            Property<T> old = activeProperty;
            activeProperty = getter.get();
            activePropertyChanged(old);
        }
    }


    private void activePropertyChanged(Property<T> old)
    {
        if (old != null)
            old.removeListener(activePropertyListener);

        activeProperty.addListener(activePropertyListener);
    }

    public void set(T newValue)
    {
        if (newValue == null && blockNotifyIfNull)
            return;

        super.set(newValue);
        if (activeProperty != null && activeProperty.getValue() != newValue)
        {
            activeProperty.setValue(newValue);
        }
    }
}
