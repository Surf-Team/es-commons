package ru.es.jfx.binding;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by saniller on 09.12.2016.
 */
public class ESInt extends ESProperty<Integer>
{
    public ESInt(int defaultVal)
    {
        super(defaultVal);
    }

    public static ESInt multProperty(ESProperty<Integer> property, double mult)
    {
        ESInt ret = new ESInt(0);
        property.addListener(new ESChangeListener<Integer>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                ret.set((int) (property.getValue()*mult));
            }
        });
        return ret;
    }

    public static ESInt multProperty(ESProperty<Integer> property, int mult)
    {
        ESInt ret = new ESInt(0);
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

    // БЫЛИ ПРОБЛЕМЫ! ТЕСТИРОВАТЬ ПРЕЖДЕ ЧЕМ ИСПОЛЬЗОВАТЬ
    public static ESInt addProperty(ESProperty<Integer> property, int add)
    {
        ESInt ret = new ESInt(0);
        property.addListener(new ESChangeListener<Integer>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                int newVal = property.getValue()+add;
                if (ret.getValue() != newVal)
                    ret.set(newVal);

                //Log.warning("Test addProperty: Changed (1)");
            }
        });
        ret.addListener(new ChangeListener<Integer>()
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                int newVal = newValue - add;
                if (property.getValue() != newVal)
                    property.set(newValue);

                //Log.warning("Test addProperty: Changed (2)");
            }
        });
        return ret;
    }

    // wrapper from int to long
    public ESInt(ESProperty<Long> longESProperty)
    {
        longESProperty.addListener(new ESChangeListener<Long>(true) {
            @Override
            public void changed(ObservableValue<? extends Long> observable, Long oldValue, Long newValue)
            {
                ESInt.this.set(newValue.intValue());
            }
        });
        this.addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                long v = newValue.longValue();
                if (longESProperty.getValue() != v)
                    longESProperty.set(v);
            }
        });
    }
}
