package ru.es.jfx.components;

import ru.es.lang.ESGetter;
import ru.es.lang.RevercibleConverter;
import ru.es.jfx.binding.ESBindings;
import ru.es.jfx.binding.ESProperty;
import ru.es.util.ListUtils;
import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saniller on 26.07.2016.
 */
public class ESFXChoiceBox<T> extends ChoiceBox<T>
{
    public static EventHandler<KeyEvent> consumerEvent = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent event)
        {
            event.consume();
        }
    };

    // NULL не поддерживаются ни где
    private ESFXChoiceBox(ObservableList<T> items)
    {
        super(items);
        this.addEventHandler(KeyEvent.ANY, consumerEvent);
    }

    private ESFXChoiceBox()
    {
        super();
        this.addEventHandler(KeyEvent.ANY, consumerEvent);
    }

    public ESFXChoiceBox(Property<T> property, T[] variants, StringConverter<T> converter)
    {
        this(FXCollections.observableArrayList(ListUtils.arrayToList(variants)));
        valueProperty().bindBidirectional(property);
        converterProperty().set(converter);

    }

    public ESFXChoiceBox(Property<T> property, ObservableList<T> items, StringConverter<T> converter)
    {
        this(items);
        valueProperty().bindBidirectional(property);
        converterProperty().set(converter);
    }


    public ESFXChoiceBox(Property<T> property, ObservableList<T> items)
    {
        this(items);
        valueProperty().bindBidirectional(property);
    }

    public ESFXChoiceBox(Property<T> property, StringConverter<T> converter)
    {
        this();
        converterProperty().set(converter);
        valueProperty().bindBidirectional(property);
    }

    public static<T> ESFXChoiceBox<Choice<T>> createNullableFixedChoiceBox(ESProperty<T> property, List<T> items, StringConverter<T> converter)
    {
        Map<Choice<T>, T> choiceToValue = new HashMap<>();
        Map<T, Choice<T>> valueToChoice = new HashMap<>();
        ObservableList<Choice<T>> choices = FXCollections.observableArrayList();

        Choice<T> nullChoice = new Choice<T>(null);
        choiceToValue.put(nullChoice, null);
        valueToChoice.put(null, nullChoice);
        choices.add(nullChoice);

        for (T item : items)
        {
            Choice<T> ch = new Choice<T>(item);
            choiceToValue.put(ch, item);
            valueToChoice.put(item, ch);
            choices.add(ch);
        }

        StringConverter<Choice<T>> internalConverter = new StringConverter<Choice<T>>() {
            @Override
            public String toString(Choice<T> object)
            {
                return converter.toString(object.get());
            }

            @Override
            public Choice<T> fromString(String string)
            {
                return null;
            }
        };

        ESProperty<Choice<T>> iternalProperty = new ESProperty<>();
        ESBindings.bindWithConvert(property, iternalProperty, new RevercibleConverter<T, Choice<T>>() {
            @Override
            public Choice<T> convertA(T src)
            {
                return valueToChoice.get(src);
            }

            @Override
            public T convertB(Choice<T> src)
            {
                return choiceToValue.get(src);
            }
        });

        return new ESFXChoiceBox<Choice<T>>(iternalProperty, choices, internalConverter);
    }

    public static class Choice<T> implements ESGetter<T>
    {
        final T t;
        public Choice(T t)
        {
            this.t = t;
        }

        @Override
        public T get()
        {
            return t;
        }
    }
}
