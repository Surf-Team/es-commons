package ru.es.jfx.components;

import ru.es.jfx.components.menu.ESFXCustomMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.layout.Pane;

/**
 * Created by saniller on 23.03.2017.
 */
@Deprecated
public class ESToolWindows
{
    public static ContextMenu createToolWindowAsMenu(Pane pane)
    {
        ESFXCustomMenuItem menuitem = new ESFXCustomMenuItem(pane, false);

        ContextMenu menu = new ContextMenu();
        menu.getStyleClass().addAll("context-window");
        menu.getItems().addAll(menuitem);

        return menu;
    }
}
