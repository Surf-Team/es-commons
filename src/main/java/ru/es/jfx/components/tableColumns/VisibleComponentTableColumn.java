package ru.es.jfx.components.tableColumns;

import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Region;
import javafx.util.Callback;

/**
 * Created by saniller on 21.09.2016.
 */
public abstract class VisibleComponentTableColumn<S,T> extends TableColumn<S,T>
{
    public VisibleComponentTableColumn(String text, Callback<CellDataFeatures<S, T>, ObservableValue<T>> cellValueFactory)
    {
        super(text);
        setCellValueFactory(cellValueFactory);

        setCellFactory(new Callback<TableColumn<S, T>, TableCell<S, T>>()
        {
            @Override
            public TableCell<S, T> call(TableColumn<S, T> param)
            {
                return new TextFieldTableCell<S, T>()
                {
                    public Node getNode()
                    {
                        Region node;

                        ObservableValue<T> value = getTableColumn().getCellObservableValue(getIndex());
                        //ObservableValue<T> value = itemProperty();

                        node = createNode(this, getItem(), value);
                        node.setMaxWidth(Double.MAX_VALUE);
                        return node;
                    }

                    public void updateItem(T item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (!empty)
                            setGraphic(getNode());

                        setText("");
                    }

                    /**{

                        tableRowProperty().addListener(new ChangeListener<TableRow>()
                        {
                            @Override
                            public void changed(ObservableValue<? extends TableRow> observable, TableRow oldValue, TableRow newValue)
                            {
                                if (newValue != null)
                                    setGraphic(getNode());
                            }
                        });

                        graphicProperty().addListener(new ChangeListener<Node>()
                        {
                            @Override
                            public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue)
                            {
                                if (oldValue == newValue)
                                {
                                    //\Log.warning("oldValue == newValue == " + newValue);
                                    return;
                                }

                                if (getTableRow() != null && !getTableRow().isEmpty())
                                {
                                    //Log.warning("SET NODE");
                                    setGraphic(getNode());
                                    setText(null);
                                }
                                else if (newValue != null && (getTableRow() == null || getTableRow().isEmpty()))
                                {
                                    //Log.warning("UNSET NODE");
                                    setGraphic(null);
                                    setText(null);
                                }

                            }
                        });
                        setGraphic(getNode());
                    }                           **/

                    /**@Override
                    public void startEdit()
                    {
                        if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable())
                        {
                            return;
                        }


                        super.startEdit();
                        setText(null);
                        setGraphic(getNode());
                    }  **/
                };
            }
        });
    }

    public abstract Region createNode(final TableCell<S, T> cell, T item, ObservableValue<T> itemProperty);
}
