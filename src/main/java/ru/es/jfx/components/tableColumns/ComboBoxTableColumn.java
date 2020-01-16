package ru.es.jfx.components.tableColumns;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * Created by saniller on 21.09.2016.
 */
public class ComboBoxTableColumn<S,T> extends VisibleComponentTableColumn<S,T>
{
    ComboBox<T> comboBox;
    StringConverter<T> stringConverter;
    ObservableList<T> comboBoxValues;

    public ComboBoxTableColumn(String text, Callback<CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory, StringConverter<T> stringConverter,
                               ObservableList<T> comboBoxValues)
    {
        super(text, cellValueFactory);
        this.stringConverter = stringConverter;
        this.comboBoxValues = comboBoxValues;
    }

    @Override
    public Region createNode(final TableCell<S, T> cell, T item, ObservableValue<T> itemProperty)
    {
        comboBox = new ComboBox<T>();

        comboBox.getSelectionModel().selectedItemProperty().addListener((ov, oldValue, newValue) -> {
            if (cell.isEditing())
            {
                cell.commitEdit(newValue);
            }
        });

        comboBox.getSelectionModel().select(cell.getItem());


        comboBox.setItems(comboBoxValues);
        comboBox.setConverter(stringConverter);

        return comboBox;
    }
}
