package ru.es.jfx.componentsv2;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import ru.es.log.Log;

public class PFFocusedBorderPane extends BorderPane
{
    public PFFocusedBorderPane()
    {
        super();
        init();
    }

    public PFFocusedBorderPane(Node center)
    {
        super(center);
        init();
    }

    public PFFocusedBorderPane(Node center, Node top, Node right, Node bottom, Node left)
    {
        super(center, top, right, bottom, left);
        init();
    }

    public void init()
    {
        addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                Log.warning("PFFocusedBorderPane pane focused");
                requestFocus();
            }
        });
    }
}
