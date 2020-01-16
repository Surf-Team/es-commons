package ru.es.jfx.components;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class ESFXRow extends HBox
{
    public ESFXRow(int spacing, Node... comps)
    {
        super(spacing, comps);
        setAlignment(Pos.CENTER_LEFT);
    }

    public ESFXRow(int spacing, Pos align, Node... comps)
    {
        super(spacing, comps);
        setAlignment(align);
    }
}
