package ru.es.jfx.application;

import ru.es.jfx.components.ESFXButton;
import ru.es.jfx.components.ESFXLabel;
import ru.es.jfx.components.LogWindow;
import ru.es.jfx.components.ESButtonType;
import ru.es.lang.ESSetter;
import ru.es.log.Log;
import ru.es.thread.RunnableImpl;
import ru.es.jfx.focus.IFocusContainer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;


public class DialogManager
{
    public final Stage primariStage;
    public AUserStatus userStatus;

    public DialogManager(Stage primariStage)
    {
        this.primariStage = primariStage;
    }

    public IFocusContainer getFocusContainer()
    {
        return null;
    }

    protected void dialogInit(Alert dialog)
    {
        dialog.initStyle(StageStyle.UTILITY);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primariStage);
    }

    public void alertError(String headerText, String text)
    {
        Log.event("Alert Error: " + headerText + " " + text);
        Alert alert = new Alert(Alert.AlertType.ERROR, text);
        dialogInit(alert);
        alert.setTitle("");
        alert.getDialogPane().setHeaderText(headerText);
        alert.show();
    }

    public void alertSuccess(String headerText, String text)
    {
        Log.event("Alert Success: " + headerText + " " + text);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, text);
        alert.setTitle("");
        dialogInit(alert);
        alert.getDialogPane().setHeaderText(headerText);
        alert.show();

    }

    public void alertWarning(String headerText, String text)
    {
        Log.event("Alert Warning: " + headerText + " " + text);
        Alert alert = new Alert(Alert.AlertType.WARNING, text);
        alert.setTitle("");
        dialogInit(alert);
        alert.getDialogPane().setHeaderText(headerText);
        //alert.showAndWait()
        //        .filter(response -> response == ButtonType.OK)
        //        .ifPresent(response -> System.out.println("The alert was approved"));
        alert.show();
    }

    public void dialogConfirm(String title, String question, Runnable onConfirm)
    {
        Log.event("Dialog Confirm: " + title + " " + question);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);

        dialogInit(alert);
        alert.setHeaderText(question);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
        {
            Log.event("Dialog Confirm: OK");
            onConfirm.run();
        }
        else
        {
            Log.event("Dialog Confirm: No");
        }
    }

    public Optional<ButtonType> dialogConfirm(String title, String question)
    {
        Log.event("Dialog Confirm: " + title + " " + question);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);

        dialogInit(alert);
        alert.setHeaderText(question);

        Optional<ButtonType> result = alert.showAndWait();
        if (result != null)
        {
            if (result.get() == ButtonType.OK)
                Log.event("Dialog Confirm: OK");
            else
                Log.event("Dialog Confirm: No");
        }

        return result;
    }

    public void dialogConfirm(String title, String bigText, String smallText, Runnable onConfirm)
    {
        Platform.runLater(() -> {
            Log.event("Dialog Confirm: " + title + " " + bigText+" "+smallText);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(title);
            alert.setHeaderText(bigText);

            dialogInit(alert);
            alert.setContentText(smallText);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK)
            {
                Log.event("Dialog Confirm: OK");
                onConfirm.run();
            } else
            {
                Log.event("Dialog Confirm: No");
            }
        });
    }


    public ButtonType dialogYesNoCancel(Alert.AlertType alertType, String title, String headerText, String contextText)
    {
        Log.event("Dialog YesNoCancel: " + title + " " + headerText+" "+contextText);
        Alert alert = new Alert(alertType);
        dialogInit(alert);

        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);

        ButtonType yesButton = ButtonType.YES;
        ButtonType noButton = ButtonType.NO;
        ButtonType cancelButton = ButtonType.CANCEL;

        alert.getButtonTypes().setAll(yesButton, noButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result != null)
        {
            if (result.get() == ButtonType.YES)
                Log.event("Dialog YesNoCancel: yes");
            if (result.get() == ButtonType.NO)
                Log.event("Dialog YesNoCancel: no");
            if (result.get() == ButtonType.CANCEL)
                Log.event("Dialog YesNoCancel: cancel");
        }

        return result.get();
    }


    public void dialogEditText(String title, String text, String initText, ESSetter<String> f)
    {
        Platform.runLater(new RunnableImpl()
        {
            @Override
            public void runImpl() throws Exception
            {
                String ret = dialogEditTextWait(title, text, initText);
                if (ret != null)
                    f.set(ret);
            }
        });
    }


    public String dialogEditTextWait(String title, String text, String initText)
    {
        TextInputDialog dialog = new TextInputDialog(initText);
        dialog.setTitle(title);
        dialog.setHeaderText(text);
        //dialog.setContentText();

        dialog.getEditor().setFocusTraversable(true);
        dialog.getEditor().requestFocus();

        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent())
            return result.get();
        return null;
    }


    public Stage createSubFrame(String title, Scene scene, StageStyle stageStyle, Stage owner, boolean traceEventsToMainForm)
    {
        Stage stage = new Stage(stageStyle);
        stage.setTitle(title);

        stage.setScene(scene);
        stage.initOwner(owner);

        if (traceEventsToMainForm)
            installTraceKeyEvents(stage);

        return stage;
    }

    public Stage createSubFrame(String title, Scene scene, StageStyle stageStyle, boolean traceEventsToMainForm)
    {
        Stage stage = new Stage(stageStyle);
        stage.setTitle(title);

        stage.setScene(scene);
        stage.initOwner(primariStage);

        if (traceEventsToMainForm)
            installTraceKeyEvents(stage);

        return stage;
    }



    public Stage createSubFrame(String title, StageStyle stageStyle, Stage owner, boolean keyEventsToMainForm)
    {
        Stage stage = new Stage(stageStyle);
        stage.setTitle(title);
        stage.initOwner(owner);

        if (keyEventsToMainForm)
            installTraceKeyEvents(stage);

        return stage;
    }


    private ESButtonType temporaryReturn;


    public ESButtonType ask(String title, String text, Stage stageOwner, ESButtonType... buttonTypes)
    {
        VBox rootBox = new VBox(5);
        rootBox.setStyle("-fx-base: #CCCCCC; -fx-background-color: #DDDDDD; -fx-padding: 20px; -fx-alignment: center");
        rootBox.setFillWidth(true);
        Stage stage = createSubFrame(title, new Scene(rootBox), StageStyle.UTILITY, stageOwner, false);
        stage.initModality(Modality.WINDOW_MODAL);

        ESFXLabel label = new ESFXLabel(text);
        label.setStyle("-fx-padding: 0 0 10 0; -fx-font-size: 18px; -fx-text-fill: #555555");
        rootBox.getChildren().add(label);

        for (ESButtonType t : buttonTypes)
        {
            VBox vBox = new VBox(5);
            vBox.setStyle("-fx-background-color: #00000030; -fx-background-radius: 8px; -fx-padding: 10px");
            vBox.setAlignment(Pos.CENTER);
            Button b = new ESFXButton(t.text, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event)
                {
                    temporaryReturn = t;
                    stage.close();
                }
            });
            b.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555; -fx-min-height: 50px");
            vBox.getChildren().add(b);
            if (t.desc != null)
            {
                Label l = new Label(t.desc);
                vBox.getChildren().add(l);
                l.setStyle("-fx-text-fill: #555555");
            }
            rootBox.getChildren().add(vBox);
        }
        
        stage.showAndWait();
        return temporaryReturn;
    }


    private void installTraceKeyEvents(Stage stage)
    {
        stage.addEventHandler(KeyEvent.ANY, new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent event)
            {
                primariStage.fireEvent(event);
            }
        });

        stage.addEventHandler(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event)
            {
                primariStage.fireEvent(event);
            }
        });
    }


    public AUserStatus getUserStatus()
    {
        return userStatus;
    }

    public LogWindow createLogWindow(String title)
    {
        LogWindow logWindow = new LogWindow(title, primariStage);
        return logWindow;
    }
}
