package ru.es.jfx.memory;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;

import java.util.ArrayList;

/**
 * Created by saniller on 18.04.2017.
 *
 * ESFXWeakObject - это класс, в котором можно создавать ChangeListener на внешние сильные ссылки Property через враппер InternalWeakProperty,
 * при этом в случае необходимости объект сможет стереться из памяти, отцепив свои ChangeListener от сильной Property
 * Иначе, если привязывать ChangeListener напрямую, то объект не уничтожится ни когда
 */

public class ESFXWeakObject
{
    ArrayList<ESWeakProperty> weakProperties;

    public class InternalWeakProperty<T> extends ESWeakProperty<T>
    {
        public InternalWeakProperty(Property<T> strongProperty)
        {
            super(strongProperty);
            if (weakProperties == null)
                weakProperties = new ArrayList<ESWeakProperty>();

            weakProperties.add(this);
        }
    }

    public class ReadOnlyInternalWeakProperty<T> extends ESWeakProperty<T>
    {
        public ReadOnlyInternalWeakProperty(ReadOnlyProperty<T> strongProperty)
        {
            super(strongProperty);

            if (weakProperties == null)
                weakProperties = new ArrayList<ESWeakProperty>();

            weakProperties.add(this);
        }
    }
}
