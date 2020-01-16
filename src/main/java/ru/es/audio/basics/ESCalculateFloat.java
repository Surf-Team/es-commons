package ru.es.audio.basics;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.audio.AudioStamp;
import ru.es.audio.deviceParameter.DeviceValueProperty;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.memory.ESWeakProperty;
import javafx.beans.value.ObservableValue;

public abstract class ESCalculateFloat extends DeviceValueProperty
{
    private ESProperty<Float> input;

    ESChangeListener<Float> inputChangeListener;

    public ESCalculateFloat(ESProperty<Float> inputProp)
    {
        super(0);
        input = new ESWeakProperty<Float>(inputProp);

        inputChangeListener = new ESChangeListener<Float>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Float> observable, Float oldValue, Float newValue)
            {
                set(calcOutput(newValue));
            }
        };

        input.addListener(inputChangeListener);
    }

    public abstract float calcOutput(float newValue);

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void calcOutput(AudioStamp stamp)
    {
        set(calcOutput(input.getValue()));
    }
}
