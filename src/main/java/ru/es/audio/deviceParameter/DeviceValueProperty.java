package ru.es.audio.deviceParameter;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.audio.FloatGetter;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class DeviceValueProperty extends ESProperty<Float> implements FloatGetter
{
    public DeviceValueProperty(float value)
    {
        super(value);
        this.val = value;
    }

    float val;
    public float offset; // added

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float value()
    {
        return val + offset;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void set(Float newValue)
    {
        this.val = newValue;
        super.set(newValue);
    }

    //todo change
    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public Float getValue()
    {
        //Thread.dumpStack();
        //throw new RuntimeException("use value() instead of getValue()");
        //return val;
        return super.getValue();
    }

    //todo change
    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public Float get()
    {
        //Thread.dumpStack();
        //throw new RuntimeException("use value() instead of get()");
        //return super.get();
        return super.get();
    }                      

    public static DeviceValueProperty normalizedBoolean(ESProperty<Boolean> b)
    {
        DeviceValueProperty ret = new DeviceValueProperty(0.0f);

        b.addListener(new ESChangeListener<Boolean>(true) {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                float newVal = newValue ? 1.0f : 0f;

                if (ret.value() != newVal)
                    ret.set(newVal);
            }
        });

        ret.addListener(new ChangeListener<Float>() {
            @Override
            public void changed(ObservableValue<? extends Float> observable, Float oldValue, Float newValue)
            {
                boolean newVal = newValue == 1.0f;

                if (b.get() != newVal)
                    b.set(newVal);
            }
        });

        return ret;
    }
}
