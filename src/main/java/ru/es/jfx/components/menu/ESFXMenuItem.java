package ru.es.jfx.components.menu;

import ru.es.log.Log;
import ru.es.thread.RunnableImpl;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;

/**
 * Created by saniller on 24.06.2016.
 */
public class ESFXMenuItem extends MenuItem
{
    public ESFXMenuItem(String name, RunnableImpl onMousePress)
    {
        super(name);
        setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                onMousePress.run();
            }
        });
    }

    public ESFXMenuItem(String name, String accelerator, RunnableImpl onMousePress)
    {
        super(name);

        setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                onMousePress.run();
            }
        });

        setAccelerator(KeyCombination.keyCombination(accelerator));
    }

    public ESFXMenuItem(String name, String accelerator, EventHandler<ActionEvent> onMousePress)
    {
        super(name);
        setOnAction(onMousePress);
        setAccelerator(KeyCombination.keyCombination(accelerator));
    }

    public ESFXMenuItem(String name, boolean enabled, RunnableImpl onMousePress)
    {
        this(name, onMousePress);
        setDisable(!enabled);
    }

    public ESFXMenuItem(String name, ObservableValue<Boolean> disabled, RunnableImpl onMousePress)
    {
        this(name, onMousePress);
        disableProperty().bind(disabled);
        disableProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                Log.warning("Changed: "+newValue);
            }
        });
    }

    public ESFXMenuItem(String name, String accelerator, boolean enabled, RunnableImpl onMousePress)
    {
        this(name, onMousePress);
        setDisable(!enabled);
        setAccelerator(KeyCombination.keyCombination(accelerator));
    }

    public ESFXMenuItem(String name, String accelerator, ObservableBooleanValue enabled, RunnableImpl onMousePress)
    {
        this(name, onMousePress);
        disableProperty().bind(Bindings.not(enabled));
        setAccelerator(KeyCombination.keyCombination(accelerator));
    }

    public ESFXMenuItem(String name, EventHandler<ActionEvent> onMousePress)
    {
        super(name);

        setOnAction(onMousePress);
    }
}
