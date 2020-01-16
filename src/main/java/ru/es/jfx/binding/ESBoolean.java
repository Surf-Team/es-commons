package ru.es.jfx.binding;

import ru.es.jfx.ESXmlObject;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by saniller on 09.12.2016.
 */
public class ESBoolean extends ESProperty<Boolean>
{
    public ESBoolean(boolean value)
    {
        super(value);
        this.val = value;
    }

    boolean val;


    public boolean value()
    {
        return val;
    }

    @Override
    public void set(Boolean newValue)
    {
        this.val = newValue;
        super.set(newValue);
    }

    public ESBoolean(Boolean defaultVal)
    {
        super(defaultVal);
    }

    public static ESBoolean invert(ESProperty<Boolean> baseProp)
    {
        ESBoolean ret = new ESBoolean(!baseProp.get());
        ret.addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                baseProp.set(!newValue);
            }
        });
        baseProp.addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                ret.set(!newValue);
            }
        });
        return ret;
    }


    public static ESBoolean and(ESProperty<Boolean> ... booleans)
    {
        ESBoolean ret = new ESBoolean(false);
        for (ESProperty<Boolean> b : booleans)
        {
            b.addListener((observable, oldValue, newValue) -> {
                for (ESProperty<Boolean> b1 : booleans)
                {
                    if (!b1.get())
                    {
                        ret.set(false);
                        return;
                    }
                }
                ret.set(true);
            });
        }
        return ret;
    }

    public static ESBoolean or(ESProperty<Boolean> ... booleans)
    {
        ESBoolean ret = new ESBoolean(false);
        for (ESProperty<Boolean> b : booleans)
        {
            b.addListener((observable, oldValue, newValue) -> {
                for (ESProperty<Boolean> b1 : booleans)
                {
                    if (b1.get())
                    {
                        ret.set(true);
                        return;
                    }
                }
                ret.set(false);
            });
        }
        return ret;
    }

    public static ESBoolean fromInt(ESXmlObject.SettingValue<Integer> val, int trueVal, int falseVal)
    {
        ESBoolean ret = new ESBoolean(false);

        val.addListener(new ESChangeListener<Integer>(true) {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                if (newValue == trueVal)
                    ret.set(true);
                else if (newValue == falseVal)
                    ret.set(false);
            }
        });

        ret.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue && val.get() != trueVal)
                    val.set(trueVal);
                else if (!newValue && val.get() != falseVal)
                    val.set(falseVal);
            }
        });

        return ret;
    }

    public static ESProperty<Boolean> fromInt(Property<Integer> property, int onValue, int offValue)
    {
        ESDynamicGetter<Boolean> ret = new ESDynamicGetter<Boolean>(() -> property.getValue() == onValue, property);

        ret.addListener((observable, oldValue, newValue) -> {
            if (newValue && property.getValue() != onValue)
            {
                property.setValue(onValue);
            }
            else if (!newValue && property.getValue() != offValue)
            {
                property.setValue(offValue);
            }
        });
        
        return ret;
    }
    public static ESProperty<Boolean> fromFloat(Property<Float> property, float onValue, float offValue)
    {
        ESDynamicGetter<Boolean> ret = new ESDynamicGetter<Boolean>(() -> property.getValue() == onValue, property);

        ret.addListener((observable, oldValue, newValue) -> {
            if (newValue && property.getValue() != onValue)
            {
                property.setValue(onValue);
            }
            else if (!newValue && property.getValue() != offValue)
            {
                property.setValue(offValue);
            }
        });

        return ret;
    }
}
