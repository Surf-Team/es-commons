package ru.es.jfx.events;

import ru.es.jfx.binding.ESChangeListener;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.css.PseudoClass;
import javafx.scene.Node;

/**
 * Created by saniller on 03.09.2016.
 */
public class ESFXWatchingPseudoClass
{
    public ESFXWatchingPseudoClass(Node n, Property<Boolean> watching)
    {
        PseudoClass pseudoClass = PseudoClass.getPseudoClass("watching");

        watching.addListener(new ESChangeListener<Boolean>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue)
                {
                    n.pseudoClassStateChanged(pseudoClass, true);
                }
                else
                {
                    n.pseudoClassStateChanged(pseudoClass, false);
                }
            }
        });
    }
}
