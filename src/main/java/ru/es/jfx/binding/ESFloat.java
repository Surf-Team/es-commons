package ru.es.jfx.binding;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by saniller on 04.07.2017.
 */
public class ESFloat extends ESProperty<Float>
{
    public ESFloat(Float defaultVal)
    {
        super(defaultVal);
    }

    public static ESFloat normalizedBoolean(ESProperty<Boolean> b)
    {
        ESFloat ret = new ESFloat(0.0f);

        b.addListener(new ESChangeListener<Boolean>(true) {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                float newVal = newValue ? 1.0f : 0f;

                if (ret.get() != newVal)
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
