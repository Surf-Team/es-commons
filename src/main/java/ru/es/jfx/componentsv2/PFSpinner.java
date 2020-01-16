package ru.es.jfx.componentsv2;

import ru.es.audio.deviceParameter.ParameterInfo;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.events.ESFXRegulatorEvent;
import ru.es.log.Log;
import ru.es.util.StringConverters;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;

public class PFSpinner extends Control
{
    public final Property<Double> value = new ESProperty<>(120.0);
    public final ESFXRegulatorEvent regulatorEvent;
    public final ESProperty<StringConverter<Number>> converter = new ESProperty<>(StringConverters.twoFloatsStringConverter);
    public final ESProperty<Orientation> orientation = new ESProperty<>(Orientation.HORIZONTAL);
    public final ESProperty<Boolean> inverted = new ESProperty<>(false);

    private Label label;
    BorderPane skin;

    public PFSpinner()
    {
        skin = new BorderPane();
        TextField textField = new TextField();
        label = new Label();

        regulatorEvent = new ESFXRegulatorEvent(this, true, value, false, 20.0, 400.0, 0.05);
        regulatorEvent.divider = 60;

        orientation.addListener(new ESChangeListener<Orientation>(true) {
            @Override
            public void changed(ObservableValue<? extends Orientation> observable, Orientation oldValue, Orientation newValue)
            {
                if (newValue == Orientation.HORIZONTAL)
                {
                    label.setCursor(Cursor.H_RESIZE);
                    regulatorEvent.horizontal.set(true);
                }
                else
                {
                    label.setCursor(Cursor.W_RESIZE);
                    regulatorEvent.horizontal.set(false);
                }
            }
        });
        inverted.addListener(new ESChangeListener<Boolean>(true) {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                regulatorEvent.inverted = newValue;
            }
        });

        label.setMouseTransparent(true);
        skin.setCenter(label);
        setFocusTraversable(false);

        label.setText(value.getValue().toString());

        value.addListener((i)-> { updateValue(); });

        addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                if (event.getClickCount() == 2)
                {
                    textField.setText(value.getValue().toString());
                    skin.setCenter(textField);
                    textField.requestFocus();
                }
            }
        });

        textField.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                skin.setCenter(textField);
                textField.requestFocus();
            }
        });
        
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (!newValue)
                    skin.setCenter(label);
            }
        });

        setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event)
            {
                if (event.getCode() == KeyCode.ENTER)
                {
                    try
                    {
                        value.setValue(Double.parseDouble(textField.getText()));
                    }
                    catch (Exception e)
                    {
                        Log.warning("Wrong Text Parse");
                    }
                    skin.setCenter(label);
                }
                else if (event.getCode() == KeyCode.ESCAPE)
                {
                    skin.setCenter(label);
                }
            }
        });
    }

    public void setInfo(ParameterInfo info)
    {
        regulatorEvent.max = info.max.value();
        regulatorEvent.min = info.min.value();
        regulatorEvent.step = info.step.getValue();
        regulatorEvent.divider = ESFXRegulatorEvent.getMouseDivider(info.min.value(), info.max.value());
        converter.set(info.valueStringConverter.getValue());
        updateValue();
    }


    @Override
    protected Skin createDefaultSkin()
    {
        return new Skin(this);
    }

    static class Skin extends SkinBase<PFSpinner>
    {
        protected Skin(PFSpinner control)
        {
            super(control);
            getChildren().add(control.skin);
        }
    }

    public void updateValue()
    {
        label.setText(converter.get().toString(value.getValue()));
    }

    public Double getValue()
    {
        return value.getValue();
    }

    public void setValue(Double value)
    {
        this.value.setValue(value);
    }

    public Orientation getOrientation()
    {
        return orientation.get();
    }

    public void setOrientation(Orientation orientation)
    {
        this.orientation.set(orientation);
    }
}
