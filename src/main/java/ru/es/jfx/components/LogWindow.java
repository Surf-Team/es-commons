package ru.es.jfx.components;

import javafx.scene.control.Label;
import ru.es.thread.ESThreadPoolManager;
import ru.es.thread.RunnableImpl;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by saniller on 19.10.2016.
 */
public class LogWindow extends Stage
{
    public VBox logBox;

    public LogWindow(String title, Stage owner)
    {
        super(StageStyle.UTILITY);
        initOwner(owner);

        setTitle(title);

        logBox = new VBox(3);
        logBox.setStyle("-fx-padding: 10px; -fx-background-color: #eaeaea;");
        ScrollPane scrollPane = new ScrollPane(logBox);
        scrollPane.setStyle("-fx-background-color: #eaeaea");
        Scene sc = new Scene(scrollPane);
        scrollPane.setPrefSize(500, 500);
        logBox.getChildren().addListener(new ListChangeListener<Node>()
        {
            @Override
            public void onChanged(Change<? extends Node> c)
            {
                scrollPane.setVvalue(scrollPane.getVmax());
            }
        });

        setScene(sc);

        //MainFormFX.applyStyle(sc);
    }

    public void doLogToScreen(String text, LogType logType)
    {
        if (!Platform.isFxApplicationThread())
        {
            ESThreadPoolManager.getInstance().runLater(new RunnableImpl()
            {
                @Override
                public void runImpl() throws Exception
                {
                    if (logType == LogType.Success)
                    {
                        Label l = new Label(text);
                        l.setStyle("-fx-background-color: darkgreen");
                        logBox.getChildren().add(l);
                    }
                    else if (logType == LogType.Error)
                        logBox.getChildren().add(new ESFXLabel(text, ESFXLabel.TextColor.Alert));
                    else if (logType == LogType.Info)
                        logBox.getChildren().add(new ESFXLabel(text, ESFXLabel.TextColor.Black));
                }
            });
        }
        else
        {
            if (logType == LogType.Success)
            {
                if (logType == LogType.Success)
                {
                    Label l = new Label(text);
                    l.setStyle("-fx-background-color: darkgreen");
                    logBox.getChildren().add(l);
                }
            }
            else if (logType == LogType.Error)
                logBox.getChildren().add(new ESFXLabel(text, ESFXLabel.TextColor.Alert));
            else if (logType == LogType.Info)
                logBox.getChildren().add(new ESFXLabel(text, ESFXLabel.TextColor.Black));
        }
    }

    public boolean isEmpty()
    {
        return logBox.getChildren().isEmpty();
    }

    public void clean()
    {
        logBox.getChildren().clear();
    }

    public enum LogType
    {
        Error,
        Info,
        Success
    }
}
