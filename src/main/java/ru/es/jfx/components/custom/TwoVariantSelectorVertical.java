package ru.es.jfx.components.custom;

import ru.es.jfx.binding.ESChangeListener;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Created by saniller on 06.07.2016.
 */
public class TwoVariantSelectorVertical extends StackPane
{
    // example: PATTERN / ARRANGE

    public Property<Integer> selectedVariant;
    public Label[] labels;

    public TwoVariantSelectorVertical(Property<Integer> selectedVariant, String... variants)
    {
        this.selectedVariant = selectedVariant;

        setCursor(Cursor.HAND);

        if (variants.length == 2)
        {
            HBox hb = new HBox(5);
            hb.setStyle("-fx-pref-height: 30px;" +
                    "-fx-max-height: 30px;");
            //hb.getStyleClass().addAll("border-control");

            StackPane leftStackPane = new StackPane();
            hb.getChildren().add(leftStackPane);

            Rectangle rectangle = new Rectangle(16, 22);
            rectangle.setStyle("-fx-stroke-line-cap: round;" +
                    "-fx-stroke: -fx-black-color;" +
                    "-fx-background-color: -fx-black-color;" +
                    "-fx-arc-height: 8px;" +
                    "-fx-arc-width: 8px");
            leftStackPane.getChildren().addAll(rectangle);

            Rectangle pointer = new Rectangle(11, 8);

            pointer.setStyle("-fx-arc-height: 4px;" +
                    "-fx-arc-width: 4px;");
            pointer.getStyleClass().addAll("selected-shape-fill");
            leftStackPane.getChildren().add(pointer);

            VBox rightHBox = new VBox();
            rightHBox.setStyle("-fx-padding: 3 0 0 0");
            hb.getChildren().addAll(rightHBox);

            int index = 0;
            labels = new Label[variants.length];
            for (String s : variants)
            {
                Label l = new Label(s);
                l.getStyleClass().addAll("font-monospaced", "small");
                labels[index] = l;
                rightHBox.getChildren().add(l);
                index++;
            }

            this.getChildren().add(hb);

            this.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
            {
                @Override
                public void handle(MouseEvent event)
                {
                    event.consume();
                    int curValue = selectedVariant.getValue();
                    if (curValue < variants.length-1)
                        selectedVariant.setValue(curValue+1);
                    else
                        selectedVariant.setValue(0);
                }
            });


            selectedVariant.addListener(new ESChangeListener<Integer>(true)
            {
                @Override
                public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue)
                {
                    int newTranslateY = -5 + 10*newValue;
                    //pointer.setTranslateY(newTranslateY);

                    Timeline timeline = new Timeline();
                    KeyFrame key = new KeyFrame(Duration.millis(300), new KeyValue(pointer.translateYProperty(), newTranslateY));
                    timeline.getKeyFrames().add(key);
                    timeline.setOnFinished((ae) ->
                    {

                    });
                    timeline.play();

                    updateLabels();
                }
            });
        }
    }

    public void updateLabels()
    {
        for (int i = 0; i < labels.length; i++)
        {
            if (i == selectedVariant.getValue())
                labels[i].setStyle("-fx-text-fill: -fx-accent");
            else
                labels[i].setStyle("-fx-text-fill: -fx-mid-color");
        }
    }

}
