package ru.es.jfx.canvas;

import ru.es.jfx.binding.ESProperty;
import ru.es.thread.RunnableImpl;
import javafx.scene.canvas.Canvas;


/**
 * Created by saniller on 23.03.2017.
 */
public abstract class IUpdatingUI extends RunnableImpl
{
    public ESProperty<Boolean> pendingUpdate = new ESProperty<>(false);

    public abstract void updateUI();

    public abstract Canvas getCanvas();

    @Override
    public void runImpl() throws Exception
    {
        updateUI();
    }
}
