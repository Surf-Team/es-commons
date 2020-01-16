package ru.es.jfx.components.containers;

import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * Created by saniller on 25.07.2016.
 */
public class ESFXDoubleColumnsPane extends GridPane
{
    public ESFXDoubleColumnsPane(int columns)
    {
        for (int i = 0; i < columns; i++)
        {
            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(50);
            getColumnConstraints().add(column1);
        }
        /**ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        column1.setPercentWidth(50);
        column2.setPercentWidth(50);
        getColumnConstraints().addAll(column1, column2);**/
    }

    public ESFXDoubleColumnsPane(Node left, Node right)
    {
        this(2);
        setLeft(left);
        setRight(right);
    }

    public void setLeft(Node n)
    {
        add(n, 0, 0);
    }

    public void setRight(Node n)
    {
        add(n, 1, 0);
    }
}
