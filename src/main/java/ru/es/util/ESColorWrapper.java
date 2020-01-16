package ru.es.util;

import javafx.scene.layout.Background;

import java.awt.Color;

/**
 * Created by saniller on 04.07.2016.
 */
public class ESColorWrapper
{
    public Color awtColor;
    public javafx.scene.paint.Color fxColor;
    public String cssColor;
    public String styleBackground;
    public String styleTextFill;
    public Background background;

    public ESColorWrapper(Color c)
    {
        awtColor = c;
        fxColor = ESFXUtils.convertAWTColorToJFX(c);
        cssColor = ESFXUtils.colorToCSSColor(fxColor);
        background = ESFXUtils.getBackground(fxColor);
        styleBackground = "-fx-background-color: "+cssColor+";";
        styleTextFill = "-fx-text-fill: "+cssColor+";";
    }

    public static javafx.scene.paint.Color toFx(Color awt)
    {
        return ESFXUtils.convertAWTColorToJFX(awt);
    }
}
