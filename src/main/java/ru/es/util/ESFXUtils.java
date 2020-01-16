package ru.es.util;

import ru.es.jfx.binding.ESChangeListener;
import ru.es.lang.ESGetter;
import ru.es.jfx.binding.ESProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.*;
import javafx.util.Callback;
import javafx.util.StringConverter;
import ru.es.log.Log;

import java.io.*;

/**
 * Created by saniller on 23.06.2016.
 */
public class ESFXUtils
{
    // так же можно делать это с помощью стиля: setStyle("-fx-background-image: url(/PolyformStudio/Forms/icons/copy11.png)");
    // ex "/PolyformStudio/Forms/icons/copy11.png"
    public static Image getImageFromClasspath(String srcToResource, int w, int h)
    {
        try
        {
            ClassLoader classLoader = ESFXUtils.class.getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream(srcToResource);

            if (inputStream == null)
                if (srcToResource.startsWith("/"))
                    inputStream = classLoader.getResourceAsStream(srcToResource.substring(1));


            return new Image(inputStream, w, h, false, false);
        }
        catch (Exception e)
        {
            Log.warning(e.getMessage());
        }
        return null;
    }

    // /PolyformStudio/Forms/icons/copy11.png
    public static InputStream getResourceAsStream(String path)
    {
        ClassLoader classLoader = ESFXUtils.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(path);

        if (inputStream == null)
            if (path.startsWith("/"))
                inputStream = classLoader.getResourceAsStream(path.substring(1));

        if (inputStream == null)
            throw new RuntimeException();

        return inputStream;
    }

    public static InputStream getResourceAsStream(ClassLoader classLoader, String path)
    {
        InputStream inputStream = classLoader.getResourceAsStream(path);

        if (inputStream == null)
            if (path.startsWith("/"))
                inputStream = classLoader.getResourceAsStream(path.substring(1));

        if (inputStream == null)
            throw new RuntimeException();

        return inputStream;
    }

    public static void applyStylesheets(Scene scene, String resource)
    {
        scene.getStylesheets().add(resource);
    }

    public static Background getBackground(javafx.scene.paint.Color c)
    {
        return new Background(new BackgroundFill(c, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public static Background getBackground(Image i)
    {
        BackgroundImage myBI = new BackgroundImage(i,
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);

        return new Background(myBI);
    }

    public static Color convertAWTColorToJFX(java.awt.Color c)
    {
        return javafx.scene.paint.Color.rgb(c.getRed(), c.getGreen(), c.getBlue(), (double) c.getAlpha() / 255.0);
    }

    public static void installChoiceBox(ChoiceBox chooser, Property value, ObservableList<Integer> observableArrayList, StringConverter swingTypeConverter)
    {
        chooser.valueProperty().bindBidirectional(value);
        chooser.setItems(observableArrayList);
        chooser.converterProperty().setValue(swingTypeConverter);
        chooser.setValue(value.getValue());
    }

    static class Delta
    {
        double x, y;
    }

    public static void setStageMoveableByScene(Stage stage, Scene scene)
    {
        EventHandler<MouseEvent> e = new EventHandler<MouseEvent>()
        {
            final Delta dragDelta = new Delta();

            @Override
            public void handle(MouseEvent mouseEvent)
            {
                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
                {
                    dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                    dragDelta.y = stage.getY() - mouseEvent.getScreenY();
                }
                else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
                {

                    stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                    stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                }
            }
        };

        scene.setOnMousePressed(e);
        scene.setOnMouseDragged(e);
    }

    public static void setStageMoveableByNode(Stage stage, Node node, ESProperty<Boolean> allow)
    {
        EventHandler<MouseEvent> e = new EventHandler<MouseEvent>()
        {
            final Delta dragDelta = new Delta();

            @Override
            public void handle(MouseEvent mouseEvent)
            {
                //if (mouseEvent.getTarget() != node)
                //return;
                if (mouseEvent.isConsumed())
                    return;

                if (!allow.get())
                    return;

                if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED)
                {
                    dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                    dragDelta.y = stage.getY() - mouseEvent.getScreenY();
                }
                else if (mouseEvent.getEventType() == MouseEvent.MOUSE_DRAGGED)
                {

                    stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                    stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                }
            }
        };

        node.setOnMousePressed(e);
        node.setOnMouseDragged(e);
    }

    public static void setIconToStage(Stage stage, String srcToResource, ClassLoader classLoader)
    {
        //Log.warning("setIconToStage class loader: "+stage.getClass().getClassLoader().toString());
        //Log.warning("another class loader: "+classLoader.toString());
        //Log.warning("ESFXUtils.class class loader: "+ESFXUtils.class.getClassLoader().toString());


        InputStream inputStream = classLoader.getResourceAsStream(srcToResource);

        if (inputStream == null)
            if (srcToResource.startsWith("/"))
                inputStream = classLoader.getResourceAsStream(srcToResource.substring(1));


        if (inputStream == null)
            throw new RuntimeException("inputStream is nyll when setIconToStage");

        Image i = new Image(inputStream);


        //Image i = new Image(classLoader.getResource(srcToResource.substring(1)).toExternalForm());
        //Log.warning("icon width: "+i.getWidth());
        //Log.warning("icon height: "+i.getHeight());
        stage.getIcons().add(i);
    }

    public static Image getImage(String src, ClassLoader classLoader)
    {
        InputStream inputStream = classLoader.getResourceAsStream(src);

        if (inputStream == null)
            if (src.startsWith("/"))
                inputStream = classLoader.getResourceAsStream(src.substring(1));


        if (inputStream == null)
            throw new RuntimeException("inputStream is null when getImage");

        return new Image(inputStream);
    }


    public static ImageView getImageView(String file) throws FileNotFoundException
    {
        Image image2 = null;
        image2 = new Image(new FileInputStream(new File(file)));
        return new ImageView(image2);
    }

    public static String colorToCSSColor(javafx.scene.paint.Color c)
    {
        int r = (int) Math.round(c.getRed() * 255.0);
        int g = (int) Math.round(c.getGreen() * 255.0);
        int b = (int) Math.round(c.getBlue() * 255.0);
        return "#" + String.format("%02x%02x%02x", r, g, b);
        //return "#"+Integer.toHexString(c.hashCode()).substring(0, 6).toUpperCase();
    }

    // new FileChooser.ExtensionFilter("Project File (xml)","*.xml")
    public static File createOpenFileChooser(File initDirectory, Stage primaryStage, FileChooser.ExtensionFilter... filter)
    {
        FileChooser openProjectFileCh = new FileChooser();
        openProjectFileCh.setInitialDirectory(initDirectory);
        openProjectFileCh.setSelectedExtensionFilter(filter[0]);
        openProjectFileCh.getExtensionFilters().addAll(filter);

        try
        {
            return openProjectFileCh.showOpenDialog(primaryStage);
        }
        catch (Exception e)
        {
            Log.warning("Small error in openProjectFileCh: " + e.getMessage() + ", folder: " + initDirectory.toString());
            openProjectFileCh.setInitialDirectory(null);
            return openProjectFileCh.showOpenDialog(primaryStage);
        }
    }

    // new FileChooser.ExtensionFilter("Project File (xml)","*.xml")
    public static File createSaveFileChooser(File initDirectory, String initFileName, Window primaryStage, FileChooser.ExtensionFilter... filter)
    {
        FileChooser saveProjectFileCh = new FileChooser();
        saveProjectFileCh.setInitialDirectory(initDirectory);
        saveProjectFileCh.setInitialFileName(initFileName);
        saveProjectFileCh.setSelectedExtensionFilter(filter[0]);
        saveProjectFileCh.getExtensionFilters().addAll(filter);

        try
        {
            return saveProjectFileCh.showSaveDialog(primaryStage);
        }
        catch (Exception e)
        {
            Log.warning("Small error in createSaveFileChooser: " + e.getMessage() + ", folder: " + initDirectory.toString());
            saveProjectFileCh.setInitialDirectory(null);
            return saveProjectFileCh.showSaveDialog(primaryStage);
        }
    }

    public static File createSelectDirectoryChooser(File initDirectory, Stage primaryStage)
    {
        DirectoryChooser dirChooser = new DirectoryChooser();
        if (initDirectory != null && initDirectory.exists())
            dirChooser.setInitialDirectory(initDirectory);

        return dirChooser.showDialog(primaryStage);
    }

    public static void alertError(Stage parentStage, String headerText, String text)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, text);
        alert.setTitle("");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(parentStage);
        alert.getDialogPane().setHeaderText(headerText);
        //alert.showAndWait()
        //        .filter(response -> response == ButtonType.OK)
        //        .ifPresent(response -> System.out.println("The alert was approved"));
        alert.show();
    }

    public static void alertError(String headerText, String text)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR, text);
        alert.setTitle("");
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().setHeaderText(headerText);
        //alert.showAndWait()
        //        .filter(response -> response == ButtonType.OK)
        //        .ifPresent(response -> System.out.println("The alert was approved"));
        alert.show();
    }

    public static void showContextMenu(MouseEvent event, Node parent, ContextMenu menu)
    {
        EventHandler<MouseEvent> hideEvent = new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                menu.hide();
                //Log.warning("Menu hide");
            }
        };

        menu.setOnHidden(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                parent.removeEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);
                //Log.warning("Remove hide event");
            }
        });
        menu.setOnAutoHide(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                parent.removeEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);
                //Log.warning("Remove hide event 2");
            }
        });
        parent.addEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);
        menu.show(parent, event.getScreenX(), event.getScreenY());
        //Log.warning("Show");
    }

    public static void showContextMenu2(MouseEvent event, Node parent, ContextMenu menu)
    {
        menu.show(parent.getScene().getWindow(), event.getScreenX(), event.getScreenY());
    }

    // показ прямо под компонентом
    public static void showContextMenu(Node parent, ContextMenu menu, Side side)
    {
        EventHandler<MouseEvent> hideEvent = new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                menu.hide();
                //Log.warning("Menu hide");
            }
        };

        menu.setOnHidden(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                parent.removeEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);
                //Log.warning("Remove hide event");
            }
        });
        menu.setOnAutoHide(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                parent.removeEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);
                //Log.warning("Remove hide event 2");
            }
        });
        parent.addEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);

        menu.show(parent, side, 0, 0);

        Log.warning("Show menu");
    }

    // показ прямо под компонентом
    public static void showContextMenu(Node parent, ContextMenu menu)
    {
        EventHandler<MouseEvent> hideEvent = new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                menu.hide();
                //Log.warning("Menu hide");
            }
        };

        menu.setOnHidden(new EventHandler<WindowEvent>()
        {
            @Override
            public void handle(WindowEvent event)
            {
                parent.removeEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);
                //Log.warning("Remove hide event");
            }
        });
        menu.setOnAutoHide(new EventHandler<Event>()
        {
            @Override
            public void handle(Event event)
            {
                parent.removeEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);
                //Log.warning("Remove hide event 2");
            }
        });
        parent.addEventHandler(MouseEvent.MOUSE_PRESSED, hideEvent);

        menu.show(parent, Side.BOTTOM, 0, 0);

        Log.warning("Show menu");
    }

    public static Button createButton(String name, EventHandler<MouseEvent> onClick)
    {
        Button ret = new Button(name);
        ret.setOnMouseClicked(onClick);
        return ret;
    }

    public static Spinner<Integer> createSpinner(int min, int max, int value)
    {
        Spinner<Integer> fillPercentSpinner = new Spinner<>();
        fillPercentSpinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        SpinnerValueFactory.IntegerSpinnerValueFactory factory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, value);
        fillPercentSpinner.setValueFactory(factory);

        return fillPercentSpinner;
    }



    public static HBox createRow(int gap, Node... children)
    {
        HBox ret = new HBox(gap);
        if (children != null && children.length != 0)
            ret.getChildren().addAll(children);
        ret.setAlignment(Pos.CENTER_LEFT);

        return ret;
    }

    public static void scrollHorizontalByVertical(ScrollPane scrollPane)
    {
        scrollPane.addEventHandler(ScrollEvent.ANY, new EventHandler<ScrollEvent>()
        {
            @Override
            public void handle(ScrollEvent event)
            {
                if (Math.abs(event.getDeltaY()) > 0)
                {
                    double nodeWidth = ((Pane) scrollPane.getContent()).getWidth();
                    double hRange = scrollPane.getHmax() - scrollPane.getHmin();
                    double hPixelValue;
                    if (nodeWidth > 0.0)
                    {
                        hPixelValue = hRange / nodeWidth;
                    }
                    else
                    {
                        hPixelValue = 0.0;
                    }

                    double newValue = scrollPane.getHvalue() + (-event.getDeltaY() * 4) * hPixelValue;

                    scrollPane.setHvalue(newValue);
                    event.consume();
                }
            }
        });
    }

    public static void initFileDragEvent(Node eventSource, Node snapshotBox, Rectangle2D snapshotRect, ESGetter<File> dragFile)
    {
        eventSource.setOnDragDetected(new DragEvent(eventSource, snapshotBox, snapshotRect, dragFile));
    }

    private static class DragEvent implements EventHandler<MouseEvent>
    {
        Node eventSource;
        Node snapshotBox;
        Rectangle2D snapshotRect;
        ESGetter<File> dragFile;

        public DragEvent(Node eventSource, Node snapshotBox, Rectangle2D snapshotRect, ESGetter<File> dragFile)
        {
            this.eventSource = eventSource;
            this.snapshotBox = snapshotBox;
            this.snapshotRect = snapshotRect;
            this.dragFile = dragFile;
        }

        @Override
        public void handle(MouseEvent event)
        {
            Dragboard dragboard = eventSource.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent clipboardContent = new ClipboardContent();
            clipboardContent.putFiles(ListUtils.createList(dragFile.get()));
            dragboard.setContent(clipboardContent);

            SnapshotParameters snapshotParameters = new SnapshotParameters();
            snapshotParameters.setViewport(snapshotRect);
            dragboard.setDragView(snapshotBox.snapshot(snapshotParameters, null));
        }
    }

    public static Border createBorder(Color color, double w)
    {
        return new Border(new BorderStroke(color, BorderStrokeStyle.NONE, CornerRadii.EMPTY, new BorderWidths(w)));
    }

    public static Border createBorder(Color color, double a, double b, double c, double d)
    {
        return new Border(new BorderStroke(color, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(a, b, c, d)));
    }


    public static<T> Callback<ListView<T>, ListCell<T>> createCellFactoryStringConverter(StringConverter<T> converter)
    {
        return
                new Callback<ListView<T>, ListCell<T>>()
                {
                    @Override
                    public ListCell<T> call(ListView<T> listView)
                    {
                        return new ListCell<T>()
                        {
                            @Override
                            public void updateItem(T item, boolean empty)
                            {
                                super.updateItem(item, empty);
                                String name = null;

                                if (item != null && !empty)
                                    name = converter.toString(item);//item.getAbsolutePath();

                                this.setText(name);
                                setGraphic(null);
                            }
                        };
                    }
                };
    }

    public static void massChangeListener(InvalidationListener listener, Observable... onChange)
    {
        for (Observable l : onChange)
        {
            l.addListener(listener);
        }
        listener.invalidated(null);
    }

    public static void createSelectedPseudoClass(Property<Boolean> selected, Node n)
    {
        PseudoClass pseudoClass = PseudoClass.getPseudoClass("selected");

        selected.addListener(new ESChangeListener<Boolean>(true)
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue)
                {
                    n.pseudoClassStateChanged(pseudoClass, true);
                }
                else
                {
                    n.pseudoClassStateChanged(pseudoClass, false);
                }
            }
        });
        n.pseudoClassStateChanged(pseudoClass, selected.getValue());
    }


    public static void setScrollSens(ScrollPane sp, double sens)
    {
        sp.addEventFilter(ScrollEvent.SCROLL, event -> {

            /*
             ** if we're completely visible then do nothing....
             ** we only consume an event that we've used.
             */
            double nodeHeight = sp.getContent().getLayoutBounds().getHeight();
            double visibleAmount = sp.getLayoutBounds().getHeight();

            //Log.warning("nh: "+nodeHeight+", va: "+visibleAmount);

            visibleAmount /= nodeHeight;

            if (visibleAmount < sp.getVmax()) {
                double vRange = sp.getVmax()-sp.getVmin();
                double vPixelValue;
                if (nodeHeight > 0.0) {
                    vPixelValue = vRange / nodeHeight;
                }
                else {
                    vPixelValue = 0.0;
                }

                //Log.warning("delta y: "+event.getDeltaY());

                double val = sp.getVvalue();
                double newValue = val+(-event.getDeltaY()*sens)*vPixelValue;

                if ((event.getDeltaY() > 0.0 && val > sp.getVmin()) ||
                        (event.getDeltaY() < 0.0 && val < sp.getVmax())) {
                    sp.setVvalue(newValue);
                    event.consume();
                }
            }

            /*if (hsb.getVisibleAmount() < sp.getHmax()) {
                double hRange = sp.getHmax()-sp.getHmin();
                double hPixelValue;
                if (nodeWidth > 0.0) {
                    hPixelValue = hRange / nodeWidth;
                }
                else {
                    hPixelValue = 0.0;
                }

                double newValue = sp.getHvalue()+(-event.getDeltaX())*hPixelValue;

                if ((event.getDeltaX() > 0.0 && sp.getHvalue() > sp.getHmin()) ||
                        (event.getDeltaX() < 0.0 && sp.getHvalue() < sp.getHmin())) {
                    sp.setHvalue(newValue);
                    event.consume();
                }
            } */
        });
    }

    public static void increment(Property<Integer> p)
    {
        p.setValue(p.getValue() + 1);
    }

    public static void clipChildren(Region region) {

        final Rectangle outputClip = new Rectangle();
        //outputClip.setArcWidth(arc);
        //outputClip.setArcHeight(arc);
        region.setClip(outputClip);

        region.layoutBoundsProperty().addListener((ov, oldValue, newValue) -> {
            outputClip.setWidth(newValue.getWidth());
            outputClip.setHeight(newValue.getHeight());
        });
    }

}
