package ru.es.jfx.componentsv2;

import com.sun.javafx.util.Utils;
import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.componentsv2.zoomable.ZoomableInfo;
import ru.es.jfx.events.ESCursorUtils;
import ru.es.jfx.canvas.IPaintFunction;
import ru.es.jfx.canvas.IUpdatingUI;
import ru.es.jfx.canvas.PaintingPane;
import ru.es.math.ESMath;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Screen;

/**
 * Created by saniller on 03.12.2016.
 */
public class PFScrollBar extends PaintingPane
{
    public final ZoomableInfo info = new ZoomableInfo();

    public ESProperty<Paint> moveableColor = new ESProperty<>(Color.rgb(90, 90, 90));
    public SimpleObjectProperty<Insets> moveableInserts = new SimpleObjectProperty<>(new Insets(4, 4, 4, 4));
    public SimpleObjectProperty<Orientation> orientationProperty = new SimpleObjectProperty<>(Orientation.HORIZONTAL);

    enum ScrollBarEvent
    {
        MOVE, START_RESIZE, END_RESIZE
    }

    public boolean mouseEntered = false;
    public boolean mousePressed = false;

    public PFScrollBar()
    {
        setMinSize(10,10);
        setCursor(Cursor.OPEN_HAND);

        IUpdatingUI iUpdatingUI = createLayer(new IPaintFunction() {
            @Override
            public void paintComponent(GraphicsContext gr, double width, double height)
            {
                gr.clearRect(0,0,width,height);

                //gr.setFill(border);
                //gr.fillRect(0, 0, width, height);

                // процент стартовой точки - это отношение
                // стартовое отношение - стартовый тик, делённый на максимум

                final boolean zoomEnabled = info.zoomEnabled.getValue();
                final double startVisible = info.value.get();
                final double min = info.min.get();
                final double max = info.max.get();
                final double zoom = info.visibleSize.get();

                //Log.warning("zoom: "+zoom+", start: "+startVisible);

                // реальные старты
                double visibleSize = zoomEnabled ? (getEndVisible() - startVisible) : zoom;
                double allSize = max - min;
                double startPercent = (startVisible- min) / allSize;
                if (!zoomEnabled)
                {
                    startPercent *= (max - min) - visibleSize;
                }

                double endPercent = (getEndVisible()- min) / allSize;
                if (!zoomEnabled)
                {
                    endPercent = startPercent + visibleSize / (max - min);
                }
                // подкручиваем, чтобы не касалось конца
                //startPercent *= ((double) (allSize - visibleSize) / (double) allSize);
                //endPercent *= ((double) (allSize - visibleSize) / (double) allSize);
                //startPercent *= (1-visiblePercent);
                //endPercent = visiblePercent + endPercent*(1-visiblePercent);
                // |......[........].............|
                // |...................[........]|
                // |[........]..................]|
                // стартовое значение колебается от 0 до 1 умноженное на (1 минус видимая часть((визмакс-визмин) / (макс-мин)))
                // конечное значение колебается от процента ((визмакс-визмин)/(макс-мин) до него же, умноженного на 0..1
                // ищем процент, занимаемый началом и процент, занимаемый концом
                /**
                 long min = getMin();
                 long max = getMax();

                 long start = getStartVisible();
                 long end = getEndVisible();
                 double startPercent = 1 - (double) (max - start) / (double) (max - min);
                 double endPercent = (double) (end - min) / (double) (max - min);**/

                //if (endPercent > 1)// этого не должно быть. НО оно нужно почему то сейчас при вертикальном скролл баре. куда то уходит дальше скролл, если до него не дошло дело
//            endPercent = 1;


                if (!mouseEntered && !mousePressed)
                    gr.setFill(moveableColor.get());
                else
                    gr.setFill(((Color) moveableColor.get()).deriveColor(0,1,1.3,1));


                Insets moveableInserts = PFScrollBar.this.moveableInserts.get();

                if (getOrientation() == Orientation.HORIZONTAL)
                {
                    int startX = (int) (width * startPercent);
                    int endX = (int) (width * endPercent);
                    double w = ESMath.max(10, endX - startX - moveableInserts.getLeft() - moveableInserts.getRight());

                    gr.fillRect(startX + moveableInserts.getLeft(),
                            moveableInserts.getTop(),
                            w,
                            height - moveableInserts.getTop() - moveableInserts.getBottom());


                    moveablePaintStart = startX + moveableInserts.getLeft();
                    moveablePaintEnd = moveablePaintStart+w;
                }
                else
                {
                    int startY = (int) (height * startPercent);
                    int endY = (int) (height * endPercent);

                    double h = ESMath.max(10, endY - startY - moveableInserts.getTop() - moveableInserts.getBottom());
                    moveablePaintStart = moveableInserts.getTop() + startY;
                    moveablePaintEnd = moveablePaintStart+h;

                    gr.fillRect(moveableInserts.getLeft(), moveableInserts.getTop() + startY,
                            width - moveableInserts.getLeft() - moveableInserts.getRight(), h);
                }

            }
        }, info.value, info.min, info.max, info.visibleSize, orientationProperty, moveableInserts,
                info.minZoom, info.zoomEnabled);
        


        this.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>()
        {
            double mousePressMainLoc = 0;
            double mousePressMainValue = 0;

            double mousePressCrossLoc = 0;
            double mousePressCrossValue = 0;
            long lastDrag = 0;

            double savedOffsetX = 0;
            double savedOffsetY = 0;

            double pressedXOnScreen = 0;
            double pressedYOnScreen = 0;

            boolean sideCheck = false;

            ScrollBarEvent currentEvent = null;


            @Override
            public void handle(MouseEvent event)
            {
                event.consume();

                if (event.getEventType() == MouseEvent.MOUSE_ENTERED)
                {
                    mouseEntered = true;
                    iUpdatingUI.updateUI();
                }
                if (event.getEventType() == MouseEvent.MOUSE_EXITED)
                {
                    mouseEntered = false;
                    iUpdatingUI.updateUI();
                }
                if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
                {
                    mousePressed = true;
                    iUpdatingUI.updateUI();
                }
                if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
                {
                    mousePressed = false;
                    iUpdatingUI.updateUI();
                }



                final boolean zoomEnabled = info.zoomEnabled.getValue();
                final double startVisible = info.value.get();
                final double min = info.min.get();
                final double max = info.max.get();
                final double zoom = info.visibleSize.get();
                final double minZoom = info.minZoom.getValue().doubleValue();

                boolean horizontal = getOrientation() == Orientation.HORIZONTAL;

                if (event.getEventType() == MouseEvent.MOUSE_PRESSED ||
                        event.getEventType() == MouseEvent.MOUSE_MOVED ||
                        event.getEventType() == MouseEvent.MOUSE_RELEASED)
                {
                    currentEvent = ScrollBarEvent.MOVE;

                    if (zoomEnabled)
                    {
                        if ((horizontal ? event.getX() : event.getY()) >= moveablePaintStart - 4 &&
                                (horizontal ? event.getX() : event.getY()) <= moveablePaintStart + 4)
                        {
                            currentEvent = ScrollBarEvent.START_RESIZE;
                        }
                        else if ((horizontal ? event.getX() : event.getY()) >= moveablePaintEnd - 4 &&
                                (horizontal ? event.getX() : event.getY()) <= moveablePaintEnd + 4)
                        {
                            currentEvent = ScrollBarEvent.END_RESIZE;
                        }
                    }

                    if (currentEvent == ScrollBarEvent.MOVE)
                        PFScrollBar.this.setCursor(Cursor.DEFAULT);
                    else if (currentEvent == ScrollBarEvent.START_RESIZE)
                        PFScrollBar.this.setCursor(horizontal ? Cursor.W_RESIZE : Cursor.S_RESIZE);
                    else if (currentEvent == ScrollBarEvent.END_RESIZE)
                        PFScrollBar.this.setCursor(horizontal ? Cursor.E_RESIZE : Cursor.N_RESIZE);
                }

                /**if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
                {
                    ESCursorUtils.moveCursor(pressedXOnScreen, pressedYOnScreen);
                } **/

                if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
                {
                    if (currentEvent == ScrollBarEvent.MOVE)
                        PFScrollBar.this.setCursor(Cursor.NONE);
                }

                if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
                {
                    mousePressMainValue = startVisible;
                    mousePressMainLoc = horizontal ? event.getX() : event.getY();

                    mousePressCrossValue = zoom;
                    mousePressCrossLoc = horizontal ? event.getY() : event.getX();

                    savedOffsetX = 0;
                    savedOffsetY = 0;


                    pressedXOnScreen = event.getScreenX();
                    pressedYOnScreen = event.getScreenY();
                }
                else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED)
                {
                    if (lastDrag + 35 > System.currentTimeMillis())
                        return;

                    if (currentEvent == ScrollBarEvent.START_RESIZE || currentEvent == ScrollBarEvent.END_RESIZE)
                    {
                        double currentValue = convertEventToTick(event.getX(), event.getY());

                        if (currentEvent == ScrollBarEvent.START_RESIZE)
                        {
                            double valueChanged = mousePressMainValue - currentValue;
                            setMain(currentValue);
                            setCross(mousePressCrossValue + valueChanged);
                        }
                        else
                        {
                            double valueChanged = mousePressMainValue + mousePressCrossValue - currentValue;
                            setCross(mousePressCrossValue - valueChanged);
                        }
                    }
                    else if (currentEvent == ScrollBarEvent.MOVE)
                    {
                        double x = event.getX() + savedOffsetX;
                        double y = event.getY() + savedOffsetY;

                        if (zoomEnabled)
                        {
                            // mustJumpX / mustJumpY нужны для сенсорного экрана, иначе если не делать - то с сенсором ведёт себя плохо
                            boolean mustJumpX = event.getScreenX() <= 1 ||
                                    Screen.getPrimary().getVisualBounds().getWidth() - 5 < event.getScreenX();
                            boolean mustJumpY = event.getScreenY() <= 1 ||
                                    Screen.getPrimary().getVisualBounds().getHeight() - 5 < event.getScreenY();

                            if (sideCheck || mustJumpX)
                            {
                                if (Math.abs(event.getScreenX() - pressedXOnScreen) > 10 && mustJumpX)
                                {
                                    double xModed = pressedXOnScreen - event.getScreenX();
                                    savedOffsetX -= xModed;
                                    ESCursorUtils.moveCursor(pressedXOnScreen, event.getScreenY());
                                }
                                sideCheck = false;
                            }
                            else if (!sideCheck || mustJumpY)
                            {
                                if (Math.abs(event.getScreenY() - pressedYOnScreen) > 10 && mustJumpY)
                                {
                                    double yModed = pressedYOnScreen - event.getScreenY();
                                    savedOffsetY -= yModed;
                                    ESCursorUtils.moveCursor(event.getScreenX(), pressedYOnScreen);
                                }
                                sideCheck = true;
                            }
                        }

                        lastDrag = System.currentTimeMillis();

                        double changeX = (horizontal ? x : y) - mousePressMainLoc;

                        double xChangeAmount = 0;
                        if (horizontal)
                            xChangeAmount = getWidth() - getMoveableSize();
                        else
                            xChangeAmount = getHeight() - getMoveableSize();

                        double changePercent = changeX / xChangeAmount;

                        if (zoomEnabled)
                        {
                            if (zoom < (max - min) / 3.0)
                            {
                                changePercent *= zoom / (max - min / 3.0);
                                // если зум в 4 раза меньше всей части, то уменьшаем процент
                            }
                        }

                        double valuesSize = max - min - (zoomEnabled ? zoom : 0);
                        double changeSize = valuesSize * changePercent;
                        //Log.warning("startVis: " + startVisible.get() + " min: " + min.get() + ", max: " + max.get());


                        double changeYLocation = (horizontal ? y : x) - mousePressCrossLoc;

                        boolean invertedY = true;
                        if (invertedY)
                            changeYLocation = -changeYLocation;

                        double ySize = 100;
                        double changePercentY = changeYLocation / ySize;
                        double valuesSizeY = getFullSize();

                        double zoomPercent = zoom / (max - min);
                        //Log.warning("zoomPercent: " + zoomPercent);

                        double changeSizeY = (valuesSizeY * changePercentY) * zoomPercent;

                        double newZoom = ESMath.constrain(mousePressCrossValue + changeSizeY / 4, minZoom, getFullSize());
                        setCross(newZoom);
                        //System.out.println("newZoom: "+newZoom+", mousePressCrossValue: "+mousePressCrossValue+", changeSizeY: "+changeSizeY);

                        double newValue = 0;
                        if (newZoom != minZoom && newZoom != getFullSize() && zoomEnabled)
                            newValue = mousePressMainValue + changeSize - changeSizeY / 8;
                        else
                            newValue = mousePressMainValue + changeSize;
                        setMain(newValue);


                        mousePressMainValue = info.value.get();
                        mousePressMainLoc = horizontal ? x : y;

                        mousePressCrossValue = info.visibleSize.get();
                        mousePressCrossLoc = horizontal ? y : x;
                    }
                }
            }

            public void setMain(double newValue)
            {
                if (Double.isNaN(newValue))
                {
                    newValue = 0;
                }
                info.value.setValue(ESMath.constrain(newValue, info.min.get(),
                        info.max.get() - (info.zoomEnabled.getValue() ? info.visibleSize.get() : 0)));
            }

            public void setCross(double newValue)
            {
                if (info.zoomEnabled.getValue())
                    info.visibleSize.setValue(ESMath.constrain(newValue, info.minZoom.getValue().doubleValue(), getFullSize()));
            }
        });
    }

    private double convertEventToTick(double x, double y)
    {
        double eventPercent;
        Insets moveableInserts = PFScrollBar.this.moveableInserts.get();

        if (getOrientation() == Orientation.HORIZONTAL)
        {
            eventPercent = (x - moveableInserts.getLeft() - moveableInserts.getRight()) /
                    (getWidth() - moveableInserts.getLeft() - moveableInserts.getRight());
        }
        else
        {
            eventPercent = (y - moveableInserts.getTop() - moveableInserts.getBottom()) /
                    (getHeight() - moveableInserts.getTop() - moveableInserts.getBottom());
        }

        double currentValue = (info.max.get() - info.min.get()) * eventPercent;
        currentValue = ESMath.max(0, currentValue);
        return currentValue;
    }

    public double getMoveableSize()
    {
        double visibleSize = getEndVisible() - info.value.get();
        double allSize = info.max.get() - info.min.get();
        double startPercent = ((double) info.value.get()- info.min.get()) / allSize;
        double endPercent = ((double) getEndVisible()- info.min.get()) / allSize;
        //double visiblePercent = ((double) (visibleSize) / (double) allSize);
        //startPercent *= (1-visiblePercent);
        //endPercent = visiblePercent + endPercent*(1-visiblePercent);
        if (getOrientation() == Orientation.HORIZONTAL)
        {
            int startX = (int) (getWidth() * startPercent);
            int endX = (int) (getWidth() * endPercent);
            return endX - startX;
        }
        else
        {
            int startX = (int) (getHeight() * startPercent);
            int endX = (int) (getHeight() * endPercent);
            return endX - startX;
        }

    }


    public double moveablePaintStart = 0;
    public double moveablePaintEnd = 0;

    public double getEndVisible()
    {
        return info.value.get() + info.visibleSize.get();
    }

    public double getFullSize(){ return info.max.get() - info.min.get(); };

    public double getVisibleSize()
    {
        return info.visibleSize.get();
    }

    public double getValue()
    {
        return info.value.get();
    }

    public void setValue(double v)
    {
        info.value.setValue(v);
    }

    public Property<Number> valueProperty()
    {
        return info.value;
    }

    public double getMax()
    {
        if (info.zoomEnabled.getValue())
            return info.max.get() - info.visibleSize.get();
        else
            return info.max.get();
    }

    public double getMin()
    {
        return info.min.get();
    }

    public void installMouseScroll(Pane centerPane, boolean blockDirect, PFScrollBar horizontalScrollBar,
                                   boolean scroll, boolean zoom)
    {
        centerPane.addEventHandler(ScrollEvent.SCROLL, event -> {
            if (event.isDirect() && blockDirect)
                return;

            if (zoom && event.isControlDown() && horizontalScrollBar != null)
            {
                ctrlZoomEvent(horizontalScrollBar.info, centerPane.getWidth(), horizontalScrollBar.convertEventToTick(event.getX(), event.getY()), event.getX(),
                        event.getDeltaY());
                event.consume();
            }
            else if (scroll && !event.isControlDown())
            {
                if (getVisibleSize() < info.max.get())
                {
                    if (getOrientation() != Orientation.HORIZONTAL)
                    {
                        double nodeHeight = (info.max.get()-info.min.get()) /  info.visibleSize.get() * getHeight();
                        double vRange = info.max.get() - info.min.get();
                        double vPixelValue;
                        if (nodeHeight > 0.0)
                        {
                            vPixelValue = vRange / nodeHeight;
                        }
                        else
                        {
                            vPixelValue = 0.0;
                        }
                        double newValue = getValue() + (-event.getDeltaY()) * vPixelValue;
                        newValue = ESMath.constrain(newValue, getMin(), getMax());
                        //if (!IS_TOUCH_SUPPORTED)
                        //{
                            if ((event.getDeltaY() > 0.0 && getValue() > getMin()) ||
                                    (event.getDeltaY() < 0.0 && getValue() < getMax()))
                            {
                                setValue(newValue);
                                event.consume();
                            }
                        /**}
                        else
                        {
                            if (!(((ScrollEvent) event).isInertia()) || (((ScrollEvent) event).isInertia()) && (contentsToViewTimeline == null || contentsToViewTimeline.getStatus() == Animation.Status.STOPPED))
                            {
                                setValue(newValue);
                                if ((newValue > getMax() || newValue < getMin()) && (!mouseDown && !touchDetected))
                                {
                                    startContentsToViewport();
                                }
                                event.consume();
                            }
                        }  **/
                    }
                    else
                    {
                        double nodeWidth = (info.max.get()-info.min.get()) /  info.visibleSize.get() * getWidth();
                        //double hRange = getSkinnable().getHmax() - getSkinnable().getHmin();
                        double hRange =  info.max.get() - info.min.get();
                        double hPixelValue;
                        if (nodeWidth > 0.0)
                        {
                            hPixelValue = hRange / nodeWidth;
                        }
                        else
                        {
                            hPixelValue = 0.0;
                        }

                        double newValue = getValue() + (-event.getDeltaX()) * hPixelValue;
                        newValue = ESMath.constrain(newValue, getMin(), getMax());
                        //if (!IS_TOUCH_SUPPORTED)
                        //{
                            if ((event.getDeltaX() > 0.0 && getValue() > getMin() ) ||
                                    (event.getDeltaX() < 0.0 && getValue() < getMax()))
                            {
                                setValue(newValue);
                                event.consume();
                            }
                        /**}
                        else
                        {
                            if (!(((ScrollEvent) event).isInertia()) || (((ScrollEvent) event).isInertia()) && (contentsToViewTimeline == null || contentsToViewTimeline.getStatus() == Animation.Status.STOPPED))
                            {
                                setValue(newValue);

                                if ((newValue > getMax() || newValue < getMin()) && (!mouseDown && !touchDetected))
                                {
                                    startContentsToViewport();
                                }
                                event.consume();
                            }
                        }  **/
                    }
                }
            }
        });
    }

    public static void ctrlZoomEvent(ZoomableInfo info, double width, double mouseOnTick, double x, double deltaY)
    {
        boolean up = deltaY > 0;


        double visibleSize = info.visibleSize.get();
        double newVisibleSize = visibleSize * (up ? 0.9 : 1.1);

        double mouseCursonPart = x / width;

        info.visibleSize.setValue(ESMath.constrain(newVisibleSize,
                info.minZoom.getValue().doubleValue(),
                info.getFullSize()));

        double newStartVisible = mouseOnTick - info.visibleSize.get()/2.0;
        newStartVisible = ESMath.constrain(newStartVisible,
                info.min.get(),
                info.max.get());

        double newMouseOnTick = newStartVisible + (newVisibleSize * mouseCursonPart);

        newStartVisible -= (newMouseOnTick - mouseOnTick);

        newStartVisible = ESMath.constrain(newStartVisible,
                info.min.get(),
                info.max.get());

        if (newVisibleSize > info.minZoom.getValue().doubleValue())
        {
            info.setValue(newStartVisible);
        }
    }


    public final void setVisibleAmount(double value) {
        info.visibleSize.setValue(value);
    }

    public final double getVisibleAmount() {
        return info.visibleSize.getValue().doubleValue();
    }

    public final Property<Number> visibleAmountProperty() {
        return info.visibleSize;
    }

    public void setMax(double max)
    {
        info.max.setValue(max);
    }

    public void setMin(double min)
    {
        info.min.setValue(min);
    }


    public DoubleProperty unitIncrement = new SimpleDoubleProperty(10);

    public double getUnitIncrement()
    {
        return unitIncrement.get();
    }

    public void setUnitIncrement(double unitIncrement)
    {
        this.unitIncrement.set(unitIncrement);
    }

    public void increment() {
        setValue(Utils.clamp(getMin(), getValue() + getUnitIncrement(), getMax()));
    }

    public void decrement() {
        setValue(Utils.clamp(getMin(), getValue() - getUnitIncrement(), getMax()));
    }

    public void setBlockIncrement(double d)
    {
        // пока оставил. Возможно нужно будет убрать вообще
    }

    public Paint getMoveableColor()
    {
        return moveableColor.get();
    }

    public void setMoveableColor(Paint moveableColor)
    {
        this.moveableColor.set(moveableColor);
    }

    public Orientation getOrientation()
    {
        return orientationProperty.get();
    }

    public void setOrientation(Orientation orientation)
    {
        this.orientationProperty.set(orientation);
    }

}
