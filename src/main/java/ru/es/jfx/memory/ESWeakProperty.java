package ru.es.jfx.memory;

import ru.es.jfx.binding.ESProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

/**
 * Created by saniller on 18.04.2017.
 */

// должна быть полем класса, иначе сразу пропадёт из памяти
public class ESWeakProperty<T> extends ESProperty<T>
{
    public ESWeakProperty(Property<T> strongProperty)
    {
        super(null);
        // при использовании bind создаётся Weak ссылка, поэтому эту проперти можно использовать в объектах, подлежащих удалению из памяти
        this.bindBidirectional(strongProperty);
    }

    public ESWeakProperty(ReadOnlyProperty<T> strongProperty)
    {
        super(null);
        // при использовании bind создаётся Weak ссылка, поэтому эту проперти можно использовать в объектах, подлежащих удалению из памяти
        this.bind(strongProperty);
    }
}
