package ru.es.jfx.components.containers;

import ru.es.jfx.components.ESFXLabel;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

/**
 * Created by saniller on 25.07.2016.
 */
public class ESFXTable extends BorderPane
{
    public ESFXTable(String[] columnNames, Node[] content)
    {
        ESFXDoubleColumnsPane header = new ESFXDoubleColumnsPane(columnNames.length);
        header.setStyle("-fx-background-color: derive(-fx-base, 20%);" +
                "-fx-border-width: 1 1 0 1;" +
                "-fx-border-color: -fx-base;" +
                "-fx-background-radius: 8 8 0 0;" +
                "-fx-border-radius: 8 8 0 0");

        int index = 0;
        for (String s : columnNames)
        {
            Label outLabel = new ESFXLabel(s, "label-mid");
            outLabel.setStyle("-fx-padding: 10px");
            header.add(outLabel, index, 0);
            index++;
        }
        setTop(header);

        ESFXDoubleColumnsPane contentPane = new ESFXDoubleColumnsPane(columnNames.length);
        ScrollPane scrollPane = new ScrollPane(contentPane);
        scrollPane.fitToWidthProperty().set(true);

        index = 0;
        for (Node n : content)
        {
            contentPane.add(n, index, 0);
            index++;
        }
        setCenter(scrollPane);
    }

}
