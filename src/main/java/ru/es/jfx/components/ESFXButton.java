package ru.es.jfx.components;

import ru.es.lang.ESGetter;
import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.events.ESFXContextMenuEvent;
import ru.es.jfx.events.ESFXSelectedPseudoClass;
import ru.es.jfx.fonts.ESFontAsesome;
import ru.es.jfx.fonts.ESFonts;
import ru.es.thread.RunnableImpl;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

import java.util.Collection;


/**
 * Created by saniller on 20.07.2016.
 */
public class ESFXButton extends Button
{
    private Shape specialShape;

    public ESFXButton(String text, EventHandler<MouseEvent> event)
    {
        super(text);
        this.setOnMouseClicked(event);
    }

    public ESFXButton(String text, RunnableImpl e)
    {
        super(text);
        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                e.run();
            }
        });
    }

    public ESFXButton(ESFontAsesome.Glyph glyph, int size, Color color, EventHandler<MouseEvent> event)
    {
        super();

        //Glyph thisGlyph = ESFonts.fontAwesome.create(glyph.getChar()).size(size).color(color);
        Label l = new Label(glyph.getChar()+"");
        l.setTextFill(color);
        l.setStyle("-fx-font-family: "+ESFonts.fontAwesomeName+"; -fx-font-size: "+size);

        setGraphic(l);

        this.setOnMouseClicked(event);
    }

    public ESFXButton(ESFontAsesome.Glyph glyph, int size, EventHandler<MouseEvent> event)
    {
        super();

        //Glyph thisGlyph = ESFonts.fontAwesome.create(glyph.getChar()).size(size).color(color);
        Label l = new Label(glyph.getChar()+"");
        l.setStyle("-fx-text-fill: -fx-text-base-color");
        l.setStyle("-fx-font-family: "+ESFonts.fontAwesomeName+"; -fx-font-size: "+size);

        setGraphic(l);

        this.setOnMouseClicked(event);
    }

    public ESFXButton(ESFontAsesome.Glyph glyph, int size)
    {
        super();

        //Glyph thisGlyph = ESFonts.fontAwesome.create(glyph.getChar()).size(size).color(color);
        Label l = new Label(glyph.getChar()+"");
        l.setStyle("-fx-text-fill: -fx-text-base-color");
        l.setStyle("-fx-font-family: "+ESFonts.fontAwesomeName+"; -fx-font-size: "+size);

        setGraphic(l);
    }

    public ESFXButton(ESFontAsesome.Glyph glyph, int size, Color color, String toolTip, EventHandler<MouseEvent> event)
    {
        super();
        this.setTooltip(new Tooltip(toolTip));

        //Glyph thisGlyph = ESFonts.fontAwesome.create(glyph.getChar()).size(size).color(color);

        Label l = new Label(glyph.getChar()+"");
        l.setTextFill(color);
        l.setStyle("-fx-font-family: "+ESFonts.fontAwesomeName+"; -fx-font-size: "+size);

        setGraphic(l);
        this.setOnMouseClicked(event);
    }

    public ESFXButton(ESProperty<String> text, EventHandler<MouseEvent> event)
    {
        super();
        this.textProperty().bindBidirectional(text);
        this.setOnMouseClicked(event);
    }

    public ESFXButton(String text, int height, EventHandler<MouseEvent> event)
    {
        super(text);
        this.setOnMouseClicked(event);
        setPrefHeight(height);
    }

/*/    public ESFXButton(String text, Side side, MenuItem... menuItems)
    {
        super(text);
        new ESFXContextMenuEvent(this, side, menuItems);
    }*/

    public ESFXButton(String text)
    {
        super(text);
    }

    // для кнопки - быстрого всплывающего меню
    public ESFXButton(String text, Side side, ESGetter<Collection<MenuItem>> menuItems)
    {
        super(text);
        new ESFXContextMenuEvent(this, side, menuItems);
    }


    public ESFXButton(ESProperty<Boolean> selectedProperty)
    {
        new ESFXSelectedPseudoClass(this, selectedProperty);
    }


    public enum Style
    {
        TransperentControlDark, TransperentControlDarker
    }

    public void initStype(Style style)
    {
        if (style == Style.TransperentControlDark)
            getStyleClass().addAll("transperent-control-dark");
        if (style == Style.TransperentControlDarker)
            getStyleClass().addAll("transperent-control-darker");
    }
}
