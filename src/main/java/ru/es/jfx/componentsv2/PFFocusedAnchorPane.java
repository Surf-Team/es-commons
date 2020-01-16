package ru.es.jfx.componentsv2;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import ru.es.log.Log;

public class PFFocusedAnchorPane extends AnchorPane
{
    public PFFocusedAnchorPane()
    {
        super();
        init();
    }

    public PFFocusedAnchorPane(Node... children)
    {
        super(children);
        init();
    }

    public void init()
    {
        addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                Log.warning("PFFocusedAnchorPane pane focused");
                requestFocus();
            }
        });
    }
}
