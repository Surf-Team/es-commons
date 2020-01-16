package ru.es.jfx.events;

import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 * Created by saniller on 14.07.2016.
 */
public class ESFXDraggingPseudoClass
{
    public ESFXDraggingPseudoClass(Node n)
    {
        PseudoClass dragging = PseudoClass.getPseudoClass("dragging");

        n.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                n.pseudoClassStateChanged(dragging, true);
            }
        });
        n.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                n.pseudoClassStateChanged(dragging, false);
            }
        });
    }
}
