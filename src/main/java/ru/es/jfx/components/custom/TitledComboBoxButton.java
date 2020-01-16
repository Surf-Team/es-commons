package ru.es.jfx.components.custom;

import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.events.ESFXComboBoxEvent;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Created by saniller on 14.07.2016.
 */
public class TitledComboBoxButton<T> extends HBox
{
    public Label valueLabel;

    public static enum Size
    {
        Standart,
        Small
    }

    public static enum ValueColor
    {
        Standart,
        Darker,
        Black
    }

    public TitledComboBoxButton(String firstLine, String secondLine, Size size, ValueColor valueColor)
    {
        super(4);
        //setStyle("-fx-alignment: bottom-left;");
        setStyle("-fx-alignment: center-left;");
        getStyleClass().addAll("transperent-control");

        VBox leftBox = new VBox();
        getChildren().add(leftBox);
        leftBox.setStyle("-fx-alignment: center");

        Polygon fullShape = new Polygon(0,0,  3,5,  6,0);

        fullShape.getStyleClass().addAll("shape-darker");
        fullShape.setStyle("-fx-stroke-width: 1px");

        leftBox.getChildren().add(fullShape);


        valueLabel = new Label();
        if (size == Size.Standart)
        {
            valueLabel.getStyleClass().addAll("bigger");
        }
        else if (size == Size.Small)
        {

        }

        if (valueColor == ValueColor.Standart)
        {
            valueLabel.getStyleClass().addAll("value");
        }
        else if (valueColor == ValueColor.Darker)
        {
            valueLabel.getStyleClass().addAll("shape-darker");
        }
        else if (valueColor == ValueColor.Black)
        {
            valueLabel.getStyleClass().addAll("shape-black");
        }

        getChildren().add(valueLabel);
        //valueLabel.setPrefWidth(fixedValueWidth);
        valueLabel.setStyle("-fx-alignment: baseline-right");

        if (!secondLine.isEmpty())
        {
            VBox rightBox = new VBox();
            getChildren().add(rightBox);
            rightBox.setStyle("-fx-alignment: bottom-left");

            if (!firstLine.isEmpty())
            {
                Label firstLineLabel = new Label(firstLine);
                firstLineLabel.getStyleClass().addAll("tiny", "label-darker");
                firstLineLabel.setStyle("-fx-padding: 0 0 0 0");
                rightBox.getChildren().add(firstLineLabel);
            }

            if (!secondLine.isEmpty())
            {
                Label secondLineLabel = new Label(secondLine);
                secondLineLabel.getStyleClass().addAll("tiny", "label-darker");
                secondLineLabel.setStyle("-fx-padding: -4 0 0 0");
                rightBox.getChildren().add(secondLineLabel);
            }
        }
        else
        {
            Label firstLineLabel = new Label(firstLine);
            getChildren().add(firstLineLabel);
            firstLineLabel.getStyleClass().addAll("tiny", "label-darker");
        }
    }

    // для показа меню
    public TitledComboBoxButton(String firstLine, String secondLine, int fixedValueWidth, List<T> items, Property<Integer> indexOfItems)
    {
        this(firstLine, secondLine, Size.Standart, ValueColor.Standart);

        indexOfItems.addListener(new ESChangeListener<Integer>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                valueLabel.setText("" + items.get(indexOfItems.getValue()));
            }
        });

        valueLabel.setPrefWidth(fixedValueWidth);

        new ESFXComboBoxEvent(this, Side.BOTTOM, items, indexOfItems);
    }


    public TitledComboBoxButton(Node mainForm, String firstLine, String secondLine, int fixedValueWidth,
                                List<T> items, Property<T> selectedItem, StringConverter<T> stringConverter)
    {
        this(firstLine, secondLine, Size.Standart, ValueColor.Standart);

        selectedItem.addListener(new ESChangeListener<T>(true)
        {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                valueLabel.setText(stringConverter.toString(selectedItem.getValue()));
            }
        });

        valueLabel.setPrefWidth(fixedValueWidth);

        new ESFXComboBoxEvent(this, mainForm, Side.BOTTOM, items, selectedItem, stringConverter);
    }



    // для показа меню
    public TitledComboBoxButton(String firstLine, String secondLine, Size size, ValueColor valueColor, Property<Integer> indexOfItems, String... items)
    {
        this(firstLine, secondLine, size, valueColor);

        indexOfItems.addListener(new ESChangeListener<Integer>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                valueLabel.setText("" + items[indexOfItems.getValue()]);
            }
        });

        //valueLabel.setPrefWidth(fixedValueWidth);

        new ESFXComboBoxEvent(this, Side.BOTTOM, indexOfItems, items);
    }

    // для показа меню
    public TitledComboBoxButton(Size size, ValueColor valueColor, Property<Integer> indexOfItems, String... items)
    {
        this("", "", size, valueColor);

        indexOfItems.addListener(new ESChangeListener<Integer>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                valueLabel.setText("" + items[indexOfItems.getValue()]);
            }
        });

        //valueLabel.setPrefWidth(fixedValueWidth);

        new ESFXComboBoxEvent(this, Side.BOTTOM, indexOfItems, items);
    }

    // для показа меню
    public TitledComboBoxButton(Size size, ValueColor valueColor, Property<Integer> indexOfItems, StringConverter<Integer> stringConverter, List<Integer> items)
    {
        this("", "", size, valueColor);

        indexOfItems.addListener(new ESChangeListener<Integer>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
            {
                valueLabel.setText("" + stringConverter.toString(indexOfItems.getValue()));
            }
        });

        //valueLabel.setPrefWidth(fixedValueWidth);

        new ESFXComboBoxEvent(this, Side.BOTTOM, indexOfItems, stringConverter, items);
    }

    // для показа всплывающего окошка
    public TitledComboBoxButton(String firstLine, String secondLine, Node panelToShow, Pane panelWhereShow)
    {
        this(firstLine, secondLine, Size.Standart, ValueColor.Standart);

        ContextMenu m = new ContextMenu();
        m.getStyleClass().addAll("context-window");
        CustomMenuItem mi = new CustomMenuItem();
        mi.getStyleClass().addAll("context-window-item");
        mi.setHideOnClick(false);
        m.getItems().addAll(mi);

        mi.setContent(panelToShow);

        this.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                //p.show(MainFormFX.this, 200, 200);
                m.show(panelWhereShow, panelWhereShow.getLocalToSceneTransform().getTx(), panelWhereShow.getLocalToSceneTransform().getTy() + panelWhereShow.getHeight());
            }
        });
    }

    // для показа всплывающего окошка + показ значения
    public TitledComboBoxButton(String firstLine, String secondLine, int fixedValueWidth, Node panelToShow, Pane panelWhereShow, Property<T> value)
    {
        this(firstLine, secondLine, Size.Standart, ValueColor.Standart);

        value.addListener(new ESChangeListener<T>(true)
        {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                valueLabel.setText("" + value.getValue().toString());

            }
        });
        valueLabel.setPrefWidth(fixedValueWidth);

        ContextMenu m = new ContextMenu();
        m.getStyleClass().addAll("context-window");
        CustomMenuItem mi = new CustomMenuItem();
        mi.getStyleClass().addAll("context-window-item");
        mi.setHideOnClick(false);
        m.getItems().addAll(mi);

        mi.setContent(panelToShow);

        this.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                //p.show(MainFormFX.this, 200, 200);
                m.show(panelWhereShow, panelWhereShow.getLocalToSceneTransform().getTx(), panelWhereShow.getLocalToSceneTransform().getTy() + panelWhereShow.getHeight());
            }
        });
    }
}
