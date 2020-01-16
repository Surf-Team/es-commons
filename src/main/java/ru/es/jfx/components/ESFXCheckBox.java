package ru.es.jfx.components;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.scene.control.CheckBox;

/**
 * Created by saniller on 18.07.2016.
 */
public class ESFXCheckBox extends CheckBox
{
    public ESFXCheckBox(String text)
    {
        super(text);
    }

    public ESFXCheckBox(String text, Property<Boolean> property)
    {
        super(text);
        selectedProperty().bindBidirectional(property);
    }

    public ESFXCheckBox(String text, ObjectProperty<Boolean> property)
    {
        super(text);
        selectedProperty().bindBidirectional(property);
    }

    public ESFXCheckBox(String text, Boolean def)
    {
        super(text);
        setSelected(def);
    }

    public ESFXCheckBox(Property<Boolean> property)
    {
        selectedProperty().bindBidirectional(property);
    }
}
