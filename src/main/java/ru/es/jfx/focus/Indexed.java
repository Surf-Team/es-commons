package ru.es.jfx.focus;

import ru.es.jfx.binding.ESProperty;
import javafx.scene.layout.Pane;

/**
 * Created by saniller on 09.01.2017.
 */

// Indexed - любой объект, который мы будем перемещать по индексу среди других таких же
public interface Indexed
{
    ESProperty<Integer> getVisibleIndexProperty();

    Pane getMovablePane();

    IMovingEvent getMovingEvent();

    default int getIndexedGroup()
    {
        return 0;
    }

    default boolean alwaysBottom()
    {
        return false;
    }


}
