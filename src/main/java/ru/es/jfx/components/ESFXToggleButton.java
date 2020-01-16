package ru.es.jfx.components;

import ru.es.lang.ESSetter;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.fonts.ESFontAsesome;
import ru.es.jfx.fonts.ESFonts;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;

/**
 * Created by saniller on 22.07.2016.
 */
public class ESFXToggleButton extends ToggleButton
{
    public static enum FillType
    {
        Simple,
        Transperent,
        SquareBorder,
        ToggleBackground
    }

    public static enum ShapeColor
    {
        Simple,
        Darker
    }

    public ESFXToggleButton()
    {
        this.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                setSelected(!isSelected());
            }
        });
    }

    public ESFXToggleButton(String text1line, String text2line, int firstLineOffset, int secondLineOffset, FillType fillType, ShapeColor shapeColor,
                            Property<Boolean> value)
    {
        this(text1line, text2line, firstLineOffset, secondLineOffset, fillType, shapeColor);
        selectedProperty().bindBidirectional(value);
    }

    @Override
    public void fire()
    {
        //nothing
    }

    public ESFXToggleButton(String text1line, String text2line, int firstLineOffset, int secondLineOffset, FillType fillType, ShapeColor shapeColor)
    {
        this();
        Text l1 = new Text(text1line);
        Text l2 = new Text(text2line);

        l2.setTranslateY(11);

        //todo сделать нё не тоггле буттоном, а стак пэйном

        l1.setTranslateX(firstLineOffset);
        l2.setTranslateX(secondLineOffset);
        l1.setStyle("-fx-smooth: false;" +
                "-fx-stroke-width: 1px;");
        l2.setStyle("-fx-smooth: false;" +
                "-fx-stroke-width: 1px;");

        Shape s = Shape.union(l1, l2);

        if (shapeColor == ShapeColor.Simple)
        {
            s.getStyleClass().addAll("shape");
            s.setStyle("-fx-stroke-width: 1px;" +
                    "-fx-smooth: true");
        }
        else if (shapeColor == ShapeColor.Darker)
        {
            s.getStyleClass().addAll("shape-darker");
            s.setStyle("-fx-stroke-width: 1px;" +
                    "-fx-smooth: true");
        }

        setGraphic(s);

        getStyleClass().addAll("noshadow");

        applyFillType(fillType);
    }

    public void applyFillType(FillType fillType)
    {
        if (fillType == FillType.Transperent)
        {
            getStyleClass().addAll("toggle-transparent");
            /*setStyle("-fx-scale-y: 0.9;" +
                    "-fx-scale-x: 0.9");*/
        }
        if (fillType == FillType.ToggleBackground)
        {
            getStyleClass().addAll("toggle-background");
        }
        else if (fillType == FillType.SquareBorder)
        {
            getStyleClass().addAll("transperent-control");
            setStyle("-fx-border-color: derive(-fx-base, -20%);" +
                    "-fx-border-width: 2px;" +
                    "-fx-effect: none;");
        }
    }

    // bind directional не подходит, т.к. при использовании динамических значений происходит беда
    public ESFXToggleButton(String text, Property<Boolean> value)
    {
        this.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                value.setValue(!value.getValue());
            }
        });

        this.setText(text);

        this.setSelected(value.getValue());
        value.addListener(new ESChangeListener<Boolean>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                selectedProperty().set(newValue);
            }
        });
    }


    // bind directional не подходит, т.к. при использовании динамических значений происходит беда
    public ESFXToggleButton(Property<Boolean> value)
    {
        this.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                value.setValue(!value.getValue());
            }
        });

        this.setSelected(value.getValue());
        value.addListener(new ESChangeListener<Boolean>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                selectedProperty().set(newValue);
            }
        });
    }

    public ESFXToggleButton(String text, ObjectPropertyBase<Boolean> value)
    {
        this();
        this.setText(text);
        this.selectedProperty().bindBidirectional(value);
    }

    public ESFXToggleButton(ESFontAsesome.Glyph glyph, int size, Color color, ObjectPropertyBase<Boolean> value)
    {
        this();

        //Glyph thisGlyph = ESFonts.fontAwesome.create(glyph.getChar()).size(size).color(color);

        Label l = new Label(glyph.getChar()+"");
        l.setTextFill(color);
        String baseStyle = "-fx-font-family: "+ESFonts.fontAwesomeName+"; -fx-font-size: "+size+"; ";
        l.setStyle(baseStyle);

        setGraphic(l);

        value.addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (!value.get())
                    l.setStyle(baseStyle+"-fx-text-fill: rgb("+color.getRed()*255+", "+color.getGreen()*255+", "+color.getBlue()*255+");");
                else
                    l.setStyle(baseStyle+"-fx-text-fill: -fx-accent;");

            }
        });

        if (!value.get())
            l.setStyle(baseStyle+"-fx-text-fill: rgb("+color.getRed()*255+", "+color.getGreen()*255+", "+color.getBlue()*255+");");
        else
            l.setStyle(baseStyle+"-fx-text-fill: -fx-accent;");

        this.selectedProperty().bindBidirectional(value);
    }


    public ESFXToggleButton(String text, ObjectPropertyBase<Boolean> value, ESSetter<Boolean> onSelect)
    {
        this();
        this.setText(text);
        this.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                value.setValue(!value.getValue());
                onSelect.set(selectedProperty().get());
            }
        });

        this.setSelected(value.get());
        value.addListener(new ESChangeListener<Boolean>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                selectedProperty().set(newValue);
            }
        });
    }

    /*public ESFXToggleButton(String text, ObservableList<String> list, String stringToAddOrRemove, boolean inverted)
    {
        this();
        setText(text);
        selectedProperty().setValue(list.contains(stringToAddOrRemove) != inverted);

        selectedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue == inverted)
                    list.remove(stringToAddOrRemove);
                else
                    list.add(stringToAddOrRemove);
            }
        });
    } */

    public<T> ESFXToggleButton(String text, ObservableList<T> list, T stringToAddOrRemove, boolean inverted)
    {
        this();
        setText(text);
        selectedProperty().setValue(list.contains(stringToAddOrRemove) != inverted);

        selectedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue == inverted)
                    list.remove(stringToAddOrRemove);
                else
                    list.add(stringToAddOrRemove);
            }
        });
    }


}
