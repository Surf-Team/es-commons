package ru.es.jfx.components;

import ru.es.lang.ESGetter;
import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class TipsPanel
{
    Pane customContentTmp;
    boolean isCustomContent = false;
    String lastHeaderText = "";

    public ObservableList<Node> children;
    public Property<String> headerText;
    public Property<String> contentText;

    public void install(Property<String> headerText, Property<String> contentText)
    {
        this.headerText = headerText;
        this.contentText = contentText;
    }

    public ObservableList<Node> getChildren()
    {
        return children;
    }

    public static String getContentStyle()
    {
        return "-fx-wrap-text: true; -fx-padding: 5px; -fx-text-fill: #DDDDDD; -fx-font-weight: 400;";
    }

    private void setTip(final String header, final String content)
    {
        /*if (isCustomContent)
        {
            getChildren().remove(this.customContentTmp);
            getChildren().add(this.content);
            isCustomContent = false;
        } */
        lastHeaderText = header;

        if (!header.equals(headerText.getValue()))
            headerText.setValue(header);
        if (!content.equals(contentText.getValue()))
            contentText.setValue(content);
    }

    public void setTipGlobal(String header, String content)
    {
        /*if (isCustomContent)
        {
            getChildren().remove(this.customContentTmp);
            getChildren().add(this.content);
            isCustomContent = false;
        } */

        lastHeaderText = header;

        headerText.setValue(header);
        contentText.setValue(content);
        enteredEvents.clear();
    }

    public void setCustomTip(String header, Pane customContent)
    {
        headerText.setValue(header);

        /*if (!isCustomContent)
        {
            isCustomContent = true;
            getChildren().remove(this.content);
            getChildren().add(customContent);
            this.customContentTmp = customContent;
        } */
    }

    public void removeCustomTip()
    {
        /*if (isCustomContent)
        {
            getChildren().remove(this.customContentTmp);
            getChildren().add(this.content);
            isCustomContent = false;
        } */
        headerText.setValue(lastHeaderText);
        //this.header.setText(lastHeaderText);
    }


    private class ToolTip
    {
        EventHandler<MouseEvent> handler;
        Node pane;

        public ToolTip(Node pane)
        {
            this.pane = pane;
        }
    }

    List<ToolTip> enteredEvents = new ArrayList<>();

    public void addToolTip(Node pane, final String header, final String content)
    {
        ToolTip tt = new ToolTip(pane);

        tt.handler = new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (event != null) // в ложном вызове event == null
                    enteredEvents.add(tt);

                setTip(header, content);
            }
        };

        pane.addEventHandler(MouseEvent.MOUSE_ENTERED, tt.handler);

        pane.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                enteredEvents.remove(tt);
                if (enteredEvents.isEmpty())
                    setTip("", "");
                else
                    enteredEvents.get(enteredEvents.size()-1).handler.handle(null);
            }
        });
    }

    public void addToolTip(Node pane, ESGetter<String> header, String content)
    {
        ToolTip tt = new ToolTip(pane);

        tt.handler = new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                if (event != null)
                    enteredEvents.add(tt);

                if (pane.isVisible())
                    setTip(header.get(), content);
            }
        };

        pane.addEventHandler(MouseEvent.MOUSE_ENTERED, tt.handler);
        pane.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                enteredEvents.remove(tt);
                if (enteredEvents.isEmpty())
                    setTip("", "");
                else
                    enteredEvents.get(enteredEvents.size()-1).handler.handle(null);
            }
        });
    }
}
