package ru.es.jfx.events;

import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;

import java.util.Collection;
import java.util.List;

/**
 * Created by saniller on 14.07.2016.
 */
public class ESFXComboBoxEvent<T>
{
    public ESFXComboBoxEvent(Node c, Side side, Collection<T> items, Property<Integer> indexOfItems)
    {
        ContextMenu menu = new ContextMenu();

        int index = 0;
        for (T t : items)
        {
            final int Findex = index;
            MenuItem mi = new MenuItem(t.toString());
            menu.getItems().add(mi);
            mi.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    indexOfItems.setValue(Findex);
                }
            });
            index++;
        }

        c.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (!menu.isShowing())
                    menu.show(c, side, 0, 0);
                else
                    menu.hide();
            }
        });
    }

    public ESFXComboBoxEvent(Pane button, Node mainParent, Side side, Collection<T> items, Property<T> selectedItem, StringConverter<T> stringConverter)
    {
        ContextMenu menu = new ContextMenu();

        int index = 0;
        for (T t : items)
        {
            MenuItem mi = new MenuItem(stringConverter.toString(t));
            menu.getItems().add(mi);
            mi.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    selectedItem.setValue(t);
                }
            });
            index++;
        }

        button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (!menu.isShowing())
                    menu.show(mainParent, side, event.getScreenX(), event.getScreenY());
                else
                    menu.hide();
            }
        });
    }

    public ESFXComboBoxEvent(Node c, Side side, Property<Integer> indexOfItems, String... items)
    {
        ContextMenu menu = new ContextMenu();

        int index = 0;
        for (String t : items)
        {
            final int Findex = index;
            MenuItem mi = new MenuItem(t);
            menu.getItems().add(mi);
            mi.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    indexOfItems.setValue(Findex);
                }
            });
            index++;
        }

        c.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (!menu.isShowing())
                    menu.show(c, side, 0, 0);
                else
                    menu.hide();
            }
        });
    }

    public ESFXComboBoxEvent(Node c, Side side, Property<Integer> indexOfItems, StringConverter<Integer> stringConverter, List<Integer> items)
    {
        ContextMenu menu = new ContextMenu();

        for (int i : items)
        {
            MenuItem mi = new MenuItem(stringConverter.toString(i));
            menu.getItems().add(mi);
            mi.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    indexOfItems.setValue(i);
                }
            });
        }

        c.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (!menu.isShowing())
                    menu.show(c, side, 0, 0);
                else
                    menu.hide();
            }
        });
    }
}
