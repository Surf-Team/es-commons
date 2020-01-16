package ru.es.jfx.componentsv2.scrollpane;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

@DefaultProperty("content")
public class PFScrollPane extends ScrollPane
{
    public PFScrollPane() {
        super();
    }


    public PFScrollPane(Node content) {
        this();
        setContent(content);
        setCache(false);
    }


    @Override
    protected Skin<?> createDefaultSkin()
    {
        return new PFScrollPaneSkin(this);
    }


    public ObjectProperty<Paint> moveableColor = new SimpleObjectProperty<>(Color.rgb(90, 90, 90));

    public Paint getMoveableColor()
    {
        return moveableColor.get();
    }

    public void setMoveableColor(Paint moveableColor)
    {
        this.moveableColor.set(moveableColor);
    }
}

