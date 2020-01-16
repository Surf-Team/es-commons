package ru.es.jfx.components;

import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.events.ESFXRegulatorEvent;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Created by saniller on 23.03.2017.
 */
@Deprecated
public class SizeRegulator extends BorderPane
{
    public static enum Side
    {
        Bottom,
        Top,
        Right,
        Left
    }

    public SizeRegulator(Stage stage, Side side, double min, double max, boolean showing)
    {
        if (side == Side.Bottom || side == Side.Top)
        {
            ESProperty<Double> heightProp = new ESProperty<>(stage.getHeight());
            stage.heightProperty().addListener(new ChangeListener<Number>()
            {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
                {
                    if (heightProp.get() != newValue.doubleValue())
                        heightProp.set(newValue.doubleValue());
                }
            });
            heightProp.addListener(new ChangeListener<Double>()
            {
                @Override
                public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue)
                {
                    double oldHeight = stage.getHeight();
                    stage.setHeight(newValue.intValue());
                    double deltaW = stage.getHeight() - oldHeight;

                    if (side == Side.Top)
                        stage.setY(stage.getY() - deltaW);
                }
            });

            install(heightProp, true, side == Side.Top, min, max, showing);
        }
        if (side == Side.Right || side == Side.Left)
        {
            ESProperty<Double> widthProp = new ESProperty<>(stage.getWidth());
            stage.widthProperty().addListener(new ChangeListener<Number>()
            {
                @Override
                public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
                {
                    if (widthProp.get() != newValue.doubleValue())
                        widthProp.set(newValue.doubleValue());
                }
            });
            widthProp.addListener(new ChangeListener<Double>()
            {
                @Override
                public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue)
                {
                    double oldWidth = stage.getWidth();
                    stage.setWidth(newValue.intValue());
                    double deltaW = stage.getWidth() - oldWidth;
                    if (side == Side.Left)
                        stage.setX(stage.getX() - deltaW);
                }
            });

            install(widthProp, false, side == Side.Left, min, max, showing);
        }
    }

    public SizeRegulator(Property<? extends Number> value, boolean horizontal, boolean inverted, double min, double max,
                         boolean showing)
    {
        install(value, horizontal, inverted, min, max, showing);
    }

    private void install(Property<? extends Number> value, boolean horizontal, boolean inverted, double min, double max,
                         boolean showing)
    {
        Pane inner = new Pane();

        if (horizontal)
        {
            setStyle("-fx-padding: 2px; -fx-min-height: 1px; -fx-min-width: 1px; -fx-cursor: v_resize");
            if (showing)
                inner.setStyle("-fx-min-height: 1px; -fx-max-height: 1px; -fx-background-color: #505050");
        }
        else
        {
            setStyle("-fx-padding: 2px; -fx-min-height: 1px; -fx-min-width: 1px; -fx-cursor: h_resize");
            if (showing)
                inner.setStyle("-fx-min-width: 1px; -fx-max-width: 1px; -fx-background-color: #505050");
        }
        this.setCenter(inner);

        ESFXRegulatorEvent r = new ESFXRegulatorEvent(this, !horizontal, value, inverted, min, max, 1, 1);
        r.magicCursor = false;
    }


}