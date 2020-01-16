package ru.es.jfx.binding;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by saniller on 09.12.2016.
 */
public class ESDouble extends ESProperty<Double>
{
    public ESDouble(Double defaultVal)
    {
        super(defaultVal);
    }

    public ESDouble(int defaultVal)
    {
        super((double) defaultVal);
    }

    public ESDouble(DoubleProperty doubleProperty)
    {
        super(doubleProperty.getValue());
        doubleProperty.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                if (ESDouble.this.get() != newValue.doubleValue())
                    ESDouble.this.set(newValue.doubleValue());
            }
        });
        this.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                if (doubleProperty.get() != newValue.doubleValue())
                    doubleProperty.set(newValue.doubleValue());
            }
        });
    }

    public ESDouble(ReadOnlyDoubleProperty doubleProperty)
    {
        super(doubleProperty.getValue());
        doubleProperty.addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
            {
                if (ESDouble.this.get() != newValue.doubleValue())
                    ESDouble.this.set(newValue.doubleValue());
            }
        });
    }

    public static ESDouble multDouble(ESProperty<Double> property, double mult)
    {
        ESDouble ret = new ESDouble(0);
        property.addListener(new ESChangeListener<Double>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue)
            {
                ret.set(property.getValue()*mult);
            }
        });
        return ret;
    }

    public static ESDouble multInt(ESProperty<Integer> property, double mult)
    {
        ESDouble ret = new ESDouble(0);
        property.addListener(new ESChangeListener<Integer>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                ret.set(property.getValue()*mult);
            }
        });
        return ret;
    }
}
