package ru.es.jfx.binding;

import ru.es.lang.Converter;
import ru.es.lang.ESSetter;
import ru.es.lang.RevercibleConverter;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * Created by saniller on 06.04.2017.
 */
public class ESBindings
{
    // binding с уменьшением качества до int. Участники не смогут иметь числа с запятой
    public static void bindBidirectionalIntFloat(ESProperty<Integer> actualValue, ESProperty<Float> otherValue)
    {
        otherValue.set(actualValue.getValue().floatValue());
        actualValue.addListener(new ChangeListener<Integer>()
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                if (otherValue.get().intValue() != newValue)
                    otherValue.set((float) newValue.intValue());
            }
        });
        otherValue.addListener(new ChangeListener<Float>()
        {
            @Override
            public void changed(ObservableValue<? extends Float> observable, Float oldValue, Float newValue)
            {
                if (actualValue.get() != newValue.intValue())
                    actualValue.set(newValue.intValue());
            }
        });
    }

    public static<P1,P2> void bindWithConvert(ESProperty<P1> initProperty, Property<P2> secondProperty, RevercibleConverter<P1, P2> converter)
    {
        initProperty.addListener(new ESChangeListener<P1>(true) {
            @Override
            public void changed(ObservableValue<? extends P1> observable, P1 oldValue, P1 newValue)
            {
                P2 newVal = converter.convertA(newValue);
                if (newVal != secondProperty.getValue())
                    secondProperty.setValue(newVal);
            }
        });
        secondProperty.addListener(new ChangeListener<P2>() {
            @Override
            public void changed(ObservableValue<? extends P2> observable, P2 oldValue, P2 newValue)
            {
                P1 newVal = converter.convertB(newValue);
                if (newVal != initProperty.getValue())
                    initProperty.set(newVal);
            }
        });
    }


    // Example:
    /*
        ESBindings.listenerInListener(VariationManager.getInstance().selectedVariation, new Converter<Variation, Property<Boolean>>()
        {
            @Override
            public Property<Boolean> convert(Variation src) throws Exception
            {
                return src.getPatternV(Channel.this).isMuted;
            }
        }, new ESSetter<Boolean>() {
            @Override
            public void set(Boolean newValue)
            {
                mutedInSelectedVariation.set(newValue);
            }
        });

        Example2:
        ESBindings.listenerInListener(VariationManager.getInstance().selectedVariation, mutedInSelectedVariation, new Converter<Variation, Property<Boolean>>()
        {
            @Override
            public Property<Boolean> convert(Variation src) throws Exception
            {
                return src.getPatternV(Channel.this).isMuted;
            }
        });

     */
    public static<K1,K2 extends Property<K3>, K3>  void listenerInListener(Property<K1> externalProperty, Property<K3> internalProperty, Converter<K1, K2> getFromK1)
    {
        ChangeListener<K3> listener = new ChangeListener<K3>() {
            @Override
            public void changed(ObservableValue<? extends K3> observable, K3 oldValue, K3 newValue)
            {
                if (internalProperty.getValue() != newValue)
                    internalProperty.setValue(newValue);
            }
        };

        externalProperty.addListener(new ChangeListener<K1>() {
            @Override
            public void changed(ObservableValue<? extends K1> observable, K1 oldValue, K1 newValue)
            {
                try
                {
                    K2 k2 = getFromK1.convert(newValue);

                    if (oldValue != null)
                        k2.removeListener(listener);
                    if (newValue != null)
                        k2.addListener(listener);

                    listener.changed(null, null, k2.getValue());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        // начальное значение
        try
        {
            K2 k2 = null;
            k2 = getFromK1.convert(externalProperty.getValue());
            listener.changed(null, null, k2.getValue());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        // bidirectional, т.е. для обратных назначений:
        internalProperty.addListener(new ChangeListener<K3>() {
            @Override
            public void changed(ObservableValue<? extends K3> observable, K3 oldValue, K3 newValue)
            {
                try
                {
                    K2 k2 = getFromK1.convert(externalProperty.getValue());
                    if (k2.getValue() != newValue)
                        k2.setValue(newValue);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    public static<T> ESProperty<String> createStringProperty(Property<T> property)
    {
        ESProperty<String> ret = new ESProperty<>(property.getValue().toString());

        property.addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                ret.set(newValue.toString());
            }
        });

        return ret;
    }
}
