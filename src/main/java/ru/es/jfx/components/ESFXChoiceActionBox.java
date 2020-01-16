package ru.es.jfx.components;

import ru.es.jfx.components.custom.TriangleMenuButton;
import ru.es.jfx.components.menu.ESFXCustomMenuItem;
import ru.es.jfx.components.menu.ESFXMenuItem;
import ru.es.log.Log;
import ru.es.thread.RunnableImpl;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Created by saniller on 10.08.2016.
 */
public abstract class ESFXChoiceActionBox<T> extends TriangleMenuButton
{
    boolean showEmptyMenu = false;
    String infoOnOpenIfEmpty = "Элементов нет";
    boolean showButtonOnOpenIfEmpty = false;
    String buttonTextOnOpen = "Добавить";
    RunnableImpl runnableOnButtonClickOnOpen = null;

    // для ESFXChoiceBox с динамическими данными
    private ESFXChoiceActionBox()
    {
        super(4, Side.BOTTOM, false);
        getStyleClass().addAll("custom-choice-box");
    }

    public ESFXChoiceActionBox(Property<T> value, StringConverter<T> stringConverter)
    {
        this(value, stringConverter, false);
    }

    public ESFXChoiceActionBox(Property<T> value, StringConverter<T> stringConverter, boolean allowNull)
    {
        this();
        init(value, stringConverter, allowNull);
    }



    //если используем words
    public ESFXChoiceActionBox(Property <T> value)
    {
        this();

        StringConverter<T> stringConverter = new StringConverter<T>()
        {
            @Override
            public String toString(T object)
            {
                return object.toString();
                //return Words.getWord(getItemsOnOpen().indexOf(object));
            }

            @Override
            public T fromString(String string)
            {
                return null;
            }
        };
        init(value, stringConverter, false);
    }

    Property<T> value;
    StringConverter<T> stringConverter;

    private void init(Property<T> value, StringConverter<T> stringConverter, boolean allowNull)
    {
        this.value = value;
        this.stringConverter = stringConverter;

        value.addListener(new ChangeListener<T>() {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                Platform.runLater(()-> setText(stringConverter.toString(newValue)));
            }
        });
        /**textProperty().bindBidirectional(new ESDynamicGetter<String>(new ESGetter<String>()
        {
            @Override
            public String get()
            {
                return stringConverter.toString(value.getValue());
            }
        }, value));                                                    */

        textProperty().set(stringConverter.toString(value.getValue()));
        this.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                updateText();
            }
        });

        setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                ContextMenu menu = new ContextMenu();

                if (allowNull)
                    menu.getItems().add(new ESFXMenuItem(stringConverter.toString(null), new RunnableImpl()
                    {
                        @Override
                        public void runImpl() throws Exception
                        {
                            value.setValue(null);
                            actionUsed(null);
                        }
                    }));

                List<T> list = getItemsOnOpen();
                if (!list.isEmpty() || !showEmptyMenu)
                {
                    for (T a : list)
                    {
                        ESFXMenuItem m = new ESFXMenuItem(stringConverter.toString(a), new RunnableImpl()
                        {
                            @Override
                            public void runImpl() throws Exception
                            {
                                value.setValue(a);
                                Log.warning("Setted new value. " + a.toString() + ", value: " + value.getValue().toString());
                                actionUsed(a);
                            }
                        });

                        m.setDisable(!isEnabledMenuItem(a));

                        menu.getItems().add(m);
                    }
                }
                else
                {
                    menu.getItems().add(getMenuItemOnEmptyItems(menu));
                }

                menu.show(ESFXChoiceActionBox.this, Side.BOTTOM, 0, 0);
            }
        });
    }

    public void actionUsed(T a)
    {

    }

    public void updateText()
    {
        textProperty().set(stringConverter.toString(value.getValue()));
    }

    public abstract List<T> getItemsOnOpen();

    public boolean isEnabledMenuItem(T item)
    {
        return true;
    }

    public void installEmptyMenu(String infoOnOpenIfEmpty, boolean showButtonOnOpenIfEmpty, String buttonTextOnOpen, RunnableImpl runnableOnButtonClickOnOpen)
    {
        showEmptyMenu = true;
        this.infoOnOpenIfEmpty = infoOnOpenIfEmpty;
        this.showButtonOnOpenIfEmpty = showButtonOnOpenIfEmpty;
        this.buttonTextOnOpen = buttonTextOnOpen;
        this.runnableOnButtonClickOnOpen = runnableOnButtonClickOnOpen;
    }

    public MenuItem getMenuItemOnEmptyItems(ContextMenu menu)
    {
        VBox pane = new VBox(5);
        ESFXCustomMenuItem i = new ESFXCustomMenuItem(pane, false);
        pane.getChildren().add(new ESFXLabel(infoOnOpenIfEmpty));

        if (showButtonOnOpenIfEmpty)
        {
            pane.getChildren().add(new ESFXButton(buttonTextOnOpen, new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    Log.warning("Run");
                    runnableOnButtonClickOnOpen.run();
                    menu.hide();
                }
            }));
        }
        return i;
    }
}

