package ru.es.jfx.components.custom;

import ru.es.jfx.components.menu.ESFXCustomMenuItem;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

import java.util.List;

/**
 * Created by saniller on 19.07.2016.
 */
public class TriangleMenuButton extends Button
{
    public Shape triangle;

    // стандартный размер triangleSize = 8
    public TriangleMenuButton(int triangleSize, Side sideToOpen, boolean transperent)
    {
        if (sideToOpen == Side.BOTTOM)
            triangle = new Polygon(0,0,  triangleSize/2,triangleSize-1,  triangleSize,0);
        else/** if (sideToOpen == Side.TOP)**/
            triangle = new Polygon(0,triangleSize-1,  triangleSize/2,0,  triangleSize,triangleSize-1);

        if (transperent)
        {
            getStyleClass().addAll("transperent-control");
            triangle.getStyleClass().add("filled-shape");
        }
        else
        {
            triangle.getStyleClass().addAll("shape");
        }

        setGraphic(triangle);
    }
                                                                                                    // menuItems могут изменяться без проблем
    public TriangleMenuButton(int triangleSize, Side sideToOpen, boolean transperent, List<MenuItem> menuItems)
    {
        this(triangleSize, sideToOpen, transperent);

        ContextMenu menu = new ContextMenu();

        addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (menu.isShowing())
                    menu.hide();
                else
                {
                    menu.getItems().clear();
                    menu.getItems().addAll(menuItems);
                    menu.show(TriangleMenuButton.this, sideToOpen, 0, 0);
                }
            }
        });
    }

    public TriangleMenuButton(String text, int triangleSize, Side sideToOpen, boolean transperent, List<MenuItem> menuItems)
    {
        this(triangleSize, sideToOpen, transperent, menuItems);
        setText(text);
        setAlignment(Pos.CENTER);
    }

    public TriangleMenuButton(int triangleSize, Side sideToOpen, boolean transperent, int heightWIdth, List<MenuItem> menuItems)
    {
        this(triangleSize, sideToOpen, transperent);
        this.setPrefSize(heightWIdth, heightWIdth);

        ContextMenu menu = new ContextMenu();
        addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                menu.getItems().clear();
                menu.getItems().addAll(menuItems);
                menu.show(TriangleMenuButton.this, sideToOpen, 0, 0);
            }
        });
    }
                                                                                                            // 5
    public TriangleMenuButton(int triangleSize, Side sideToOpen, boolean transperent, ESFXCustomMenuItem subWindowMenuItem)
    {
        this(triangleSize, sideToOpen, transperent);

        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().addAll("context-window");
        menu.getItems().addAll(subWindowMenuItem);

        addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (!menu.isShowing())
                    menu.show(TriangleMenuButton.this, sideToOpen, 0, 0);
                else
                    menu.hide();
            }
        });
    }

    public TriangleMenuButton(int triangleSize, Side sideToOpen, boolean transperent, EventHandler<MouseEvent> onClick)
    {
        this(triangleSize, sideToOpen, transperent);
        addEventHandler(MouseEvent.MOUSE_PRESSED, onClick);
    }

}
