package ru.es.jfx.canvas;

import javafx.scene.canvas.GraphicsContext;

/**
 * Created by saniller on 23.03.2017.
 */
public interface IPaintFunction
{
    public void paintComponent(GraphicsContext gr, double width, double height);
}
