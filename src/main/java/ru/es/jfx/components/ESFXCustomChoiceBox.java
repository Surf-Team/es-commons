package ru.es.jfx.components;

//import com.sun.istack.internal.Nullable;
import ru.es.jfx.components.menu.ESFXMenuItem;
import ru.es.lang.ESGetter;
import ru.es.lang.ESSetter;
import ru.es.lang.RevercibleConverter;
import ru.es.jfx.binding.ESBindings;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESProperty;
import ru.es.thread.RunnableImpl;
import ru.es.util.ESFXUtils;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;
import javafx.util.StringConverter;
import javolution.util.FastTable;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saniller on 13.09.2017.
 */
public class ESFXCustomChoiceBox<T> extends Button
{
    ESProperty<T> value;
    Collection<T> items;

    public ESFXCustomChoiceBox(ESProperty<T> value, Collection<T> items, /*@Nullable */StringConverter<T> stringConverter)
    {
        this.value = value;
        this.items = items;
        getStyleClass().addAll( "center-left");

        Polygon pg = new Polygon(0, 0, 3, 6, 6, 0);
        pg.setStyle("-fx-fill: -fx-accent");
        setGraphic(pg);

        value.addListener(new ESChangeListener<T>(true) {
            @Override
            public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue)
            {
                if (stringConverter != null)
                    setText(stringConverter.toString(newValue));
                else
                    setText(newValue.toString());
            }
        });

        ContextMenu m = new ContextMenu();
        /*if (staticMenu)
        {
            for (T t : getItemsToShow())
                m.getItems().add(new ESFXMenuItem(stringConverter != null ? stringConverter.toString(t) : t.toString(), new RunnableImpl() {
                    @Override
                    public void runImpl() throws Exception
                    {
                        itemSelected(t);
                    }
                }));
        } */

        ChoiceBox choiceBox = new ChoiceBox();
        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event)
            {
                preOpenMenu();
                /*if (!staticMenuF)
                { */
                    m.getItems().clear();
                    for (T t : getItemsToShow())
                        m.getItems().add(new ESFXMenuItem(stringConverter != null ? stringConverter.toString(t) : t.toString(), new RunnableImpl()
                        {
                            @Override
                            public void runImpl() throws Exception
                            {
                                itemSelected(t);
                            }
                        }));
                //}
                ESFXUtils.showContextMenu(event, ESFXCustomChoiceBox.this, m);
            }
        });
    }


    public List<ESSetter<T>> onItemSelected = new FastTable<>();

    public void preOpenMenu()
    {

    }

    private void itemSelected(T t)
    {
        value.set(t);
        for (ESSetter<T> setter : onItemSelected)
            setter.set(t);
    }

    public Collection<T> getItemsToShow()
    {
        return items;
    }

    public static<T> ESFXCustomChoiceBox<Choice<T>> createFixedChoiceBox(ESProperty<T> property, ChoiceData<T> choiceData, StringConverter<T> converter)
    {
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
                return choiceData.valueToChoice.get(src);
            }

            @Override
            public T convertB(Choice<T> src)
            {
                return choiceData.choiceToValue.get(src);
            }
        });

        return new ESFXCustomChoiceBox<Choice<T>>(iternalProperty, choiceData.choices, internalConverter);
    }


    public static<T> ChoiceData<T> createNullableChoiceData(List<T> items)
    {
        ChoiceData<T> choiceData = new ChoiceData<>(items);

        Choice<T> nullChoice = new Choice<T>(null);
        choiceData.choiceToValue.put(nullChoice, null);
        choiceData.valueToChoice.put(null, nullChoice);
        choiceData.choices.add(nullChoice);

        for (T item : items)
        {
            Choice<T> ch = new Choice<T>(item);
            choiceData.choiceToValue.put(ch, item);
            choiceData.valueToChoice.put(item, ch);
            choiceData.choices.add(ch);
        }

        return choiceData;
    }

    public static class ChoiceData<T>
    {
        // заполняется отдельно через конструктор
        Map<Choice<T>, T> choiceToValue = new HashMap<>();
        Map<T, Choice<T>> valueToChoice = new HashMap<>();
        ObservableList<Choice<T>> choices = FXCollections.observableArrayList();

        public List<T> items;

        private ChoiceData(List<T> items)
        {
            this.items = items;
        }
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
