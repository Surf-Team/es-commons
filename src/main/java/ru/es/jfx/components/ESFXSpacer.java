package ru.es.jfx.components;

import javafx.scene.layout.Pane;

/**
 * Created by saniller on 31.01.2017.
 */
public class ESFXSpacer extends Pane
{
    public ESFXSpacer(int w, int h)
    {
        setMinSize(w,h);
        setMaxSize(w,h);
        setPrefSize(w,h);
    }
}
