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
import javafx.scene.layout.HBox;
import javolution.util.FastTable;

import java.util.List;

/**
 * Created by saniller on 01.03.2017.
 */
public class ESFXVerticalTabPane extends BorderPane
{
    HBox tabsPanel;
    BorderPane contentPane;
    List<Tab> tabs = new FastTable<>();
    public ESProperty<Tab> selectedTab = new ESProperty<>(null);

    public enum Style
    {
        Big,
        Small
    }

    Style style;

    public ESFXVerticalTabPane(Style style)
    {
        this.style = style;
        tabsPanel = new HBox();
        tabsPanel.getStyleClass().addAll("vertical-tab-pane-tabs-container");

        contentPane = new BorderPane();
        if (style == Style.Big)
            contentPane.getStyleClass().addAll("vertical-tab-pane-content");
        else
            contentPane.getStyleClass().addAll("vertical-tab-pane-content-mini");         

        this.setTop(tabsPanel);
        this.setCenter(contentPane);

        selectedTab.addListener(new ESChangeListener<Tab>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
            {
                if (oldValue != null)
                {
                    contentPane.getChildren().remove(oldValue.content);
                    oldValue.selectedProperty.set(false);
                }

                if (newValue != null)
                {
                    contentPane.setCenter(newValue.content);
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

        t.tabLabel = new Label(name);
        new ESFXSelectedPseudoClass(t.tabLabel, selected);

        tabsPanel.getChildren().add(t.tabLabel);

        t.tabLabel.getStyleClass().add("vertical-tab-pane-tab");

        if (style == Style.Small)
            t.tabLabel.getStyleClass().add("vertical-tab-pane-tab-small");

        t.tabLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>()
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
        public Label tabLabel;

        public Tab(String name, Node content, ESProperty<Boolean> selectedProperty)
        {
            this.name = name;
            this.content = content;
            this.selectedProperty = selectedProperty;
        }

        void selectMe()
        {
            selectedTab.set(this);
        }
    }
}
