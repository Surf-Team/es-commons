package ru.es.jfx.components.tableColumns;

import ru.es.jfx.components.ESFXCheckBox;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Region;
import javafx.util.Callback;

/**
 * Created by saniller on 21.09.2016.
 */
public class CheckBoxTableColumn<S> extends VisibleComponentTableColumn<S, Boolean>
{
    public CheckBoxTableColumn(String text, Callback<CellDataFeatures<S, Boolean>, ObservableValue<Boolean>> cellValueFactory)
    {
        super(text, cellValueFactory);
    }

    @Override
    public Region createNode(TableCell<S, Boolean> cell, Boolean item, ObservableValue<Boolean> itemProperty)
    {
        Region ret = new ESFXCheckBox("", (Property<Boolean>) itemProperty);
        ret.setStyle("-fx-alignment: center");
        return ret;
    }
}
