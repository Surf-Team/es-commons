package ru.es.jfx.components;

import ru.es.audio.deviceParameter.IDeviceParameter;
import ru.es.lang.ESGetter;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESDynamicGetter;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

/**
 * Created by saniller on 25.07.2016.
 */
public class ESFXLabel extends Label
{
    public enum TextColor
    {
        Black,
        Darker,
        Mid,
        Light,
        Value,
        Alert,
        White
    }

    public enum TextSize
    {
        size8,
        size9,
        size10,
        size11,
        size12,
        size13,
        size14
    }

    public enum Role
    {
        TextValue,
        TinyLabelBIGwords,
        SmallLabelBigWords,
        ValueInBlackRoundedPane,
        ValueInBlackRoundedPane2,
        SmallValueInGrayRP
    }

    public ESFXLabel(String text, Role visibleRole)
    {
        if (visibleRole == Role.TextValue)
            getStyleClass().addAll("label-mid", "notbold");
        if (visibleRole == Role.TinyLabelBIGwords)
        {
            getStyleClass().addAll("tiniLabelBigWrods");
            text = text.toUpperCase();
        }
        if (visibleRole == Role.SmallLabelBigWords)
        {
            getStyleClass().addAll("SmallLabelBigWords");
            text = text.toUpperCase();
        }
        if (visibleRole == Role.ValueInBlackRoundedPane)
            getStyleClass().addAll("ValueInBlackRoundedPane");
        if (visibleRole == Role.ValueInBlackRoundedPane2)
            getStyleClass().addAll("ValueInBlackRoundedPane2");
        if (visibleRole == Role.SmallValueInGrayRP)
            getStyleClass().addAll("SmallValueInGrayRP");

        setText(text);
    }

    public ESFXLabel(IDeviceParameter parameter, Role visibleRole)
    {
        this(new ESDynamicGetter<String>(new ESGetter<String>() {
            @Override
            public String get()
            {
                return parameter.getInfo().valueStringConverter.get().toString(parameter.valueProperty().value());
            }
        }, parameter.valueProperty()), visibleRole);
    }


    public ESFXLabel(ObservableValue text, Role visibleRole)
    {
        if (visibleRole == Role.TextValue)
            getStyleClass().addAll("label-mid", "notbold");
        if (visibleRole == Role.TinyLabelBIGwords)
            getStyleClass().addAll("tiniLabelBigWrods");
        if (visibleRole == Role.SmallLabelBigWords)
            getStyleClass().addAll("SmallLabelBigWords");
        if (visibleRole == Role.ValueInBlackRoundedPane)
            getStyleClass().addAll("ValueInBlackRoundedPane");
        if (visibleRole == Role.ValueInBlackRoundedPane2)
            getStyleClass().addAll("ValueInBlackRoundedPane2");
        if (visibleRole == Role.SmallValueInGrayRP)
            getStyleClass().addAll("SmallValueInGrayRP");

        text.addListener(new ESChangeListener(true) {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue)
            {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run()
                    {
                        if (visibleRole == Role.TinyLabelBIGwords || visibleRole == Role.SmallLabelBigWords)
                            setText(newValue.toString().toUpperCase());
                        else
                            setText(newValue.toString());
                    }
                });
            }
        });


        if (visibleRole == Role.TinyLabelBIGwords || visibleRole == Role.SmallLabelBigWords)
            setText(text.getValue().toString().toUpperCase());
        else
            setText(text.getValue().toString());
    }


    public ESFXLabel(String text)
    {
        super(text);
    }

    public ESFXLabel(String text, boolean multiline)
    {
        super(text);
        if (multiline)
            setStyle("-fx-wrap-text: true;");
    }

    public ESFXLabel(String text, TextColor textColor)
    {
        this.setText(text);
        if (textColor == TextColor.Black)
            getStyleClass().addAll("label-dark");
        else if (textColor == TextColor.Darker)
            getStyleClass().addAll("label-darker");
        else if (textColor == TextColor.Mid)
            getStyleClass().addAll("label-mid");
        else if (textColor == TextColor.Light)
            getStyleClass().addAll("label-light");
        else if (textColor == TextColor.Value)
            getStyleClass().addAll("label-value");
        else if (textColor == TextColor.Alert)
            getStyleClass().addAll("label-alert");

    }

    public ESFXLabel(String text, String style)
    {
        setText(text);
        setStyle(style);
    }

    public ESFXLabel(String text, TextColor textColor, TextSize textSize)
    {
        this(text, textColor);
        if (textSize == TextSize.size8)
            getStyleClass().addAll("font-size-8");
        if (textSize == TextSize.size9)
            getStyleClass().addAll("tiny");
        if (textSize == TextSize.size10)
            getStyleClass().addAll("small");
        if (textSize == TextSize.size11)
            getStyleClass().addAll("smaller");
        if (textSize == TextSize.size12)
            getStyleClass().addAll("font-size-12");
        if (textSize == TextSize.size13)
            getStyleClass().addAll("font-size-13");
        if (textSize == TextSize.size14)
            getStyleClass().addAll("font-size-14");
    }

    public ESFXLabel(Property text, TextColor textColor, TextSize textSize)
    {
        this(text, textColor);
        if (textSize == TextSize.size8)
            getStyleClass().addAll("font-size-8");
        if (textSize == TextSize.size9)
            getStyleClass().addAll("tiny");
        if (textSize == TextSize.size10)
            getStyleClass().addAll("small");
        if (textSize == TextSize.size11)
            getStyleClass().addAll("smaller");
        if (textSize == TextSize.size12)
            getStyleClass().addAll("font-size-12");
        if (textSize == TextSize.size13)
            getStyleClass().addAll("font-size-13");
        if (textSize == TextSize.size14)
            getStyleClass().addAll("font-size-14");
    }

    public ESFXLabel(ObservableValue text, TextColor textColor)
    {
        super();
        textProperty().bind(text);

        if (textColor == TextColor.Black)
            getStyleClass().addAll("label-dark");
        else if (textColor == TextColor.Darker)
            getStyleClass().addAll("label-darker");
        else if (textColor == TextColor.Mid)
            getStyleClass().addAll("label-mid");
        else if (textColor == TextColor.Light)
            getStyleClass().addAll("label-light");
        else if (textColor == TextColor.Value)
            getStyleClass().addAll("label-value");
        else if (textColor == TextColor.Alert)
            getStyleClass().addAll("label-alert");
    }

    public ESFXLabel(Property text)
    {
        super();
        textProperty().bind(text);
    }

    public ESFXLabel(String text, String... styleClasses)
    {
        this(text);
        getStyleClass().addAll(styleClasses);
    }
}
