package ru.es.jfx.components.containers;

import ru.es.jfx.binding.ESChangeListener;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;

/**
 * Created by saniller on 19.07.2016.
 */
public class ESFXPreferencesPanel extends VBox
{
    public ESFXPreferencesPanel()
    {
        super(10);
        setStyle("-fx-padding: 10 20 10 20; -fx-text-fill: red");
        construct();
    }

    public BorderPane addSettingRow(String title, Node settingManage)
    {
        BorderPane settingRow = new BorderPane();
        Label titleLabel = new Label(title);

        titleLabel.setStyle("-fx-padding: 0 15 0 0;" +
                "-fx-wrap-text: true;" +
                "-fx-max-width: 9999;" +
                "-fx-max-height: 9999;" +
                "-fx-alignment: top-left; " +
                "-fx-text-fill: white;");

        settingRow.setCenter(titleLabel);
        settingRow.setRight(settingManage);

        getChildren().add(settingRow);
        return settingRow;
    }

    public BorderPane addSettingRowNodeToleft(String title, Node settingManage)
    {
        BorderPane settingRow = new BorderPane();
        Label titleLabel = new Label(title);

        titleLabel.setStyle("-fx-padding: 0 0 0 15;" +
                "-fx-wrap-text: true;" +
                "-fx-max-width: 9999;" +
                "-fx-max-height: 9999;" +
                "-fx-alignment: center-left");

        settingRow.setCenter(titleLabel);
        settingRow.setLeft(settingManage);

        getChildren().add(settingRow);
        return settingRow;
    }

    public BorderPane addSettingRow(Node left, Node right)
    {
        BorderPane settingRow = new BorderPane();

        //left.setStyle(left.getStyle()+";-fx-padding: 0 15 0 0;" +
                //"-fx-wrap-text: true;" +
                //"-fx-alignment: top-left;");

        settingRow.setLeft(left);
        settingRow.setRight(right);

        getChildren().add(settingRow);
        return settingRow;
    }

    public void addSettingRow(Node center)
    {
        BorderPane settingRow = new BorderPane();

        //settingRow.setStyle("-fx-padding: 0 15 0 0;");

        settingRow.setCenter(center);

        getChildren().add(settingRow);
    }

    public void addSettingRowToLeft(Node left)
    {
        BorderPane settingRow = new BorderPane();

        //settingRow.setStyle("-fx-padding: 0 15 0 0;");

        settingRow.setLeft(left);

        getChildren().add(settingRow);
    }

    public void addToCenter(Node center)
    {
        getChildren().add(center);
    }

    public void addTip(String text)
    {
        Label audioInfo = new Label();
        audioInfo.setText(text);
        audioInfo.getStyleClass().add("tip");

        getChildren().add(audioInfo);
    }

    public void addParagraph(String name)
    {
        BorderPane p = new BorderPane();
        p.setStyle("-fx-padding: 10 0 0 0");
        Label text = new Label(name);
        text.getStyleClass().add("label-mid");
        text.setStyle("-fx-font-size: 16px;");
        p.setLeft(text);

        Pane splitter = new Pane();
        int splitterH = 1;
        splitter.setMaxHeight(splitterH);
        splitter.setMinHeight(splitterH);
        splitter.setPrefHeight(splitterH);
        splitter.getStyleClass().addAll("panel-dark");
        splitter.setStyle("-fx-background-insets: 0 15 0 15;" +
                "-fx-background-radius: 3px");

        p.setCenter(splitter);

        getChildren().add(p);
    }

    public static Pane createHeader(String name)
    {
        BorderPane p = new BorderPane();
        p.setStyle("-fx-padding: 0 0 0 0");
        Label text = new Label(name);
        text.getStyleClass().addAll("label-mid", "medium");
        p.setLeft(text);

        Pane splitter = new Pane();
        int splitterH = 1;
        splitter.setMaxHeight(splitterH);
        splitter.setMinHeight(splitterH);
        splitter.setPrefHeight(splitterH);
        splitter.getStyleClass().addAll("panel-dark");
        splitter.setStyle("-fx-background-insets: 0 15 0 15;" +
                "-fx-background-radius: 3px");

        p.setCenter(splitter);
        return p;
    }

    public static Pane createFileManager(String name, Property<File> fileProperty, Node rightFileControl)
    {
        BorderPane p = new BorderPane();
        Label text = new Label(name);
        text.setStyle("-fx-padding: 0 15 0 0;" +
                "-fx-wrap-text: true;" +
                "-fx-max-width: 9999;" +
                "-fx-max-height: 9999;" +
                "-fx-alignment: top-left");
        p.setCenter(text);

        p.setRight(rightFileControl);

        Label value = new Label("");
        value.getStyleClass().addAll("label-mid", "notbold");
        fileProperty.addListener(new ESChangeListener<File>(true)
        {
            @Override
            public void changed(ObservableValue<? extends File> observable, File oldValue, File newValue)
            {
                value.setText(fileProperty.getValue().toString());
            }
        });
        p.setBottom(value);

        return p;
    }

    public void construct()
    {

    }
}
