package ru.es.jfx.events;

import ru.es.lang.ESGetter;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;

import java.util.Collection;

/**
 * Created by saniller on 03.08.2016.
 */
public class ESFXContextMenuEvent
{
    public ESFXContextMenuEvent(Node button, Side side, ESGetter<Collection<MenuItem>> menuItems)
    {
        ContextMenu menu = new ContextMenu();

        button.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                menu.getItems().clear();
                menu.getItems().addAll(menuItems.get());
                menu.show(button, side, 0, 0);
            }
        });
    }

    /*public static void show(Node button, Side side, MenuItem... menuItems)
    {
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(menuItems);
        menu.show(button, side, 0, 0);
    } */
}
