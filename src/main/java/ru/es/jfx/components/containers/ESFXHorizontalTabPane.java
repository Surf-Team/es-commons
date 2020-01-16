package ru.es.jfx.components.containers;

import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.events.ESFXSelectedPseudoClass;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import ru.es.log.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by saniller on 18.07.2016.
 */
public class ESFXHorizontalTabPane extends BorderPane
{
    VBox tabsPanel;
    VBox contentPane;
    List<Tab> tabs = new LinkedList<>();
    public ESProperty<Tab> selectedTab = new ESProperty<>(null);

    public ESFXHorizontalTabPane()
    {
        tabsPanel = new VBox();
        tabsPanel.getStyleClass().addAll("horizontal-tab-pane-tabs-container");

        contentPane = new VBox();
        contentPane.getStyleClass().addAll("horizontal-tab-pane-content");

        this.setLeft(tabsPanel);
        this.setCenter(contentPane);

        selectedTab.addListener(new ESChangeListener<Tab>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
            {
                if (oldValue != null)
                {
                    //contentPane.getChildren().remove(oldValue.content);
                    oldValue.content.setVisible(false);
                    oldValue.content.setManaged(false);
                    oldValue.selectedProperty.set(false);
                }

                if (newValue != null)
                {
                    //contentPane.setCenter(newValue.content);
                    newValue.content.setVisible(true);
                    newValue.content.setManaged(true);
                    newValue.selectedProperty.set(true);
                }
            }
        });
    }

    public Tab addTab(String name, Node content)
    {
        ESProperty<Boolean> selected = new ESProperty<>(false);
        Tab t = new Tab(name, content, selected);
        tabs.add(t);

        t.content.setVisible(false);
        t.content.setManaged(false);
        contentPane.getChildren().add(t.content);

        Label label = new Label(name);
        new ESFXSelectedPseudoClass(label, selected);

        tabsPanel.getChildren().add(label);
        label.getStyleClass().add("horizontal-tab-pane-tab");
        label.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                t.selectMe();
            }
        });

        // выбираем первый TAB
        if (tabs.size() == 1)
            t.selectMe();

        return t;
    }

    public class Tab
    {
        String name;
        Node content;
        ESProperty<Boolean> selectedProperty;

        public Tab(String name, Node content, ESProperty<Boolean> selectedProperty)
        {
            this.name = name;
            this.content = content;
            this.selectedProperty = selectedProperty;
        }

        void selectMe()
        {
            Log.event("Opening tab: "+name);
            selectedTab.set(this);
        }
    }
}
