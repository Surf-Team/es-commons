package ru.es.jfx.components.menu;

import ru.es.thread.RunnableImpl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;

/**
 * Created by saniller on 19.07.2016.
 */
public class ESFXCustomMenuItem extends CustomMenuItem
{
    public ESFXCustomMenuItem(Node panelToShow, boolean hideOnClick)
    {
        getStyleClass().addAll("context-window-item");
        setHideOnClick(hideOnClick);

        panelToShow.getStyleClass().add("context-window-content");
        
        setContent(panelToShow);
    }

    public ESFXCustomMenuItem(String text, boolean hideOnClick, RunnableImpl onClick)
    {
        setHideOnClick(hideOnClick);
        setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                onClick.run();
            }
        });

        setContent(new Label(text));
    }
}
