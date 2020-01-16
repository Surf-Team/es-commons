package ru.es.jfx.components;

import ru.es.lang.ESGetter;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.jfx.binding.ESDynamicGetter;
import ru.es.jfx.binding.ESProperty;
import ru.es.util.ListUtils;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;

/**
 * Created by saniller on 21.07.2016.
 */

/** ComboBox, который используется для выбора меняющихся значений, и для вызова ACTION при выборе значений.
 * Выбранное значение остаётся даже после изменения списка
 * Выбранное значение остаётся до изменения списка (при запуске программы, если выбранное значение было сохранено)
 **/

public abstract class ESFXComboBoxActionString<T> extends ComboBox<String>
{
    // list - чистый просматриваемый лист объектов String, которые будут доступны для выбора
    // nullValueText - текст значения null
    // monitoringProperty - цель (конечное выбираемое значение любого типа). Необходимо для проверки на null и для ChangeListener
    // getRealValueText() - реальное значение monitoringProperty. Необходимо для инициализации, например когда программа запустилась, значение не выбирали, но оно уже было установлено

    public ESFXComboBoxActionString(Property<ObservableList<String>> items, String nullValueText, ObjectPropertyBase monitoringProperty)
    {
        ESProperty<ObservableList<String>> names = new ESProperty<ObservableList<String>>(FXCollections.observableArrayList());
        // поддержка значений null, поддержка мягкого изменения листа
        ESChangeListener<ObservableList<String>> changeListener = new ESChangeListener<ObservableList<String>>(true)
        {
            @Override
            public void changed(ObservableValue<? extends ObservableList<String>> observable, ObservableList<String> oldValue, ObservableList<String> newValue)
            {
                ListUtils.modificateList(names.get(), newValue, nullValueText);
            }
        };
        items.addListener(changeListener);

        itemsProperty().bindBidirectional(names);

        promptTextProperty().bindBidirectional(new ESDynamicGetter<String>(new ESGetter<String>()
        {
            @Override
            public String get()
            {
                if (monitoringProperty.get() != null)
                    return getRealValueText();
                else
                    return nullValueText;
            }
        }, monitoringProperty));

        setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                int index = names.get().indexOf(getValue());
                if (index == 0 || index == -1)
                    onSelectNullItem();
                else
                    onSelectItem(getValue());
            }
        });
    }

    public abstract void onSelectItem(String item);

    public abstract void onSelectNullItem();

    // не учитывать null
    public abstract String getRealValueText();
}
