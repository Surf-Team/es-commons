package ru.es.jfx.components;

import javafx.beans.property.Property;
import javafx.scene.control.TextField;

public class ComponentBuilders
{
    public static TextField createTextField(Property<String> stringProperty)
    {
        TextField ret = new TextField();
        ret.textProperty().bindBidirectional(stringProperty);
        return ret;
    }
}
