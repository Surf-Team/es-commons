package ru.es.jfx.events;

import ru.es.audio.deviceParameter.DeviceValueProperty;
import ru.es.audio.deviceParameter.ParameterInfo;
import ru.es.jfx.binding.ESChangeListener;
import ru.es.lang.ESSetter;
import ru.es.jfx.binding.ESProperty;
import ru.es.math.ESMath;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Screen;

/**
 * Created by saniller on 21.10.2016.
 */
public class ESFXRegulatorEvent
{
    public ESProperty<Boolean> horizontal = new ESProperty<>(true);
    public boolean inverted;

    public Number pressedLoc = 0;
    public Number pressedMain = 0;

    public Number lastDragLocation = 0;

    private Property<? extends Number> value;
    public ESSetter<Number> deltaSetter;
    public Number lastDelta = 0;

    public Number min = Double.MIN_VALUE;
    public Number max = Double.MAX_VALUE;
    public Number divider = 1;
    public Number step = 1;

    public double dividerMod = 1.0;

    public ESSetter<Number> onEvent;


    private boolean pressedNow = false;

    // авто-divider
    public ESFXRegulatorEvent(Node c,
                              boolean horizontal,
                              Property<? extends Number> value,
                              boolean inverted,
                              Number min,
                              Number max,
                              Number step)
    {
        this(c, horizontal, value, inverted, min, max, getMouseDivider(min.doubleValue(), max.doubleValue()), step);
    }

    // авто-divider
    public ESFXRegulatorEvent(Node c,
                              boolean horizontal,
                              Property<? extends Number> value,
                              boolean inverted,
                              ParameterInfo parameterInfo)
    {
        this(c, horizontal, value, inverted, parameterInfo.min.get(), parameterInfo.max.get(),
                getMouseDivider(parameterInfo.min.value(), parameterInfo.max.value()),
                parameterInfo.step.getValue());

        dividerMod = parameterInfo.regulatorDivider;
    }


    public ESFXRegulatorEvent(Node c,
                                 boolean horizontal,
                                 Property<? extends Number> value,
                                 boolean inverted,
                                 Number min,
                                 Number max,
                                 Number divider,
                                 Number step)
    {
        this.horizontal.set(horizontal);
        this.inverted = inverted;
        this.min = min;
        this.max = max;
        this.divider = divider;
        this.step = step;
        this.value = value;

        setMouseDragEvent(c);
    }


    double savedOffsetX = 0;
    double savedOffsetY = 0;
    double pressedXOnScreen = 0;
    double pressedYOnScreen = 0;
    boolean sideCheck = false;

    public boolean magicCursor = true;

    void setMouseDragEvent(Node c)
    {
        horizontal.addListener(new ESChangeListener<Boolean>(true) {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (newValue)
                    c.setCursor(Cursor.H_RESIZE);
                else
                    c.setCursor(Cursor.V_RESIZE);
            }
        });

        c.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                savedOffsetX = 0;
                savedOffsetY = 0;
                pressedXOnScreen = event.getScreenX();
                pressedYOnScreen = event.getScreenY();
                
                if (magicCursor)
                    c.setCursor(Cursor.NONE);

                pressedNow = true;
                if (horizontal.get())
                    pressedLoc = event.getScreenX();
                else
                    pressedLoc = event.getScreenY();

                pressedMain = value.getValue().doubleValue();
                //Log.warning("OLD VALUE: "+value.getValue().doubleValue());
                lastDelta = 0;
                event.consume();

                onPress();
            }
        });
        c.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent event)
            {
                pressedNow = false;
                if (horizontal.get())
                    c.setCursor(Cursor.H_RESIZE);
                else
                    c.setCursor(Cursor.V_RESIZE);
                event.consume();

                if (magicCursor)
                    ESCursorUtils.moveCursor(pressedXOnScreen, pressedYOnScreen);

                onRelease();
            }
        });
        c.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>()
        {
            boolean lastCtrl = false;
            boolean needRecalc = false;

            @Override
            public void handle(MouseEvent event)
            {
                if ((event.isControlDown() || event.isShiftDown() || event.isAltDown()) && !lastCtrl)
                {
                    lastCtrl = true;
                    needRecalc = true;
                }
                else if (!event.isControlDown() && !event.isShiftDown() && !event.isAltDown() && lastCtrl)
                {
                    lastCtrl = false;
                    needRecalc = true;
                }

                if (needRecalc)
                {
                    savedOffsetX = 0;
                    savedOffsetY = 0;
                    pressedXOnScreen = event.getScreenX();
                    pressedYOnScreen = event.getScreenY();

                    if (horizontal.get())
                        pressedLoc = event.getScreenX();
                    else
                        pressedLoc = event.getScreenY();

                    pressedMain = value.getValue().doubleValue();
                    lastDelta = 0;
                    needRecalc = false;
                }

                if (pressedNow)
                {
                    double x = event.getScreenX() + savedOffsetX;
                    double y = event.getScreenY() + savedOffsetY;

                    double locNowMain = 0;
                    if (horizontal.get())
                        locNowMain = x;
                    else
                        locNowMain = y;

                    if (magicCursor)
                    {
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

                    lastDragLocation = locNowMain;

                    //Log.warning("locNowMain: "+locNowMain+", pressedLoc: "+pressedLoc.doubleValue()+", divide: "+divider.doubleValue());

                    double changeByMain = (locNowMain - pressedLoc.doubleValue()) / (divider.doubleValue() * dividerMod);
                    if (inverted)
                        changeByMain = -changeByMain;

                    if (event.isAltDown() || event.isControlDown() || event.isShiftDown())
                        changeByMain /= 10;

                    //Log.warning("ChangeByMain: "+changeByMain);

                    double mainNewValue = ESMath.constrain(pressedMain.doubleValue() + changeByMain,
                            min.doubleValue(), max.doubleValue());
                    if (deltaSetter != null)
                    {
                        deltaSetter.set(changeByMain - lastDelta.doubleValue());
                        lastDelta = changeByMain;
                    }
                    //Log.warning("mainNewValue: "+mainNewValue);

                    mainNewValue = ESMath.specialRound(mainNewValue, step.doubleValue());
                    //Log.warning("MainNewValue: "+mainNewValue);

                    mainNewValue = ESMath.constrain(mainNewValue, min.doubleValue(), max.doubleValue());
                    //Log.warning("rounded: "+mainNewValue);

                    //Log.warning("mainNewValue 2: "+mainNewValue);

                    if (value instanceof DeviceValueProperty)
                    {
                        ((DeviceValueProperty) value).setByUser((float) mainNewValue);
                    }
                    else if (value instanceof ESProperty)
                    {
                        ESProperty val = (ESProperty) value;
                        if (value.getValue() instanceof Integer)
                            ((ESProperty<Integer>) val).setByUser((int) mainNewValue);
                        else if (value.getValue() instanceof Float)
                            ((ESProperty<Float>) val).setByUser((float) mainNewValue);
                        else if (value.getValue() instanceof Double)
                            ((ESProperty<Double>) val).setByUser(mainNewValue);
                        else if (value.getValue() instanceof Long)
                            ((ESProperty<Long>) val).setByUser((long) mainNewValue);
                        else if (value.getValue() instanceof Short)
                            ((ESProperty<Short>) val).setByUser((short) mainNewValue);
                        else if (value.getValue() instanceof Byte)
                            ((ESProperty<Byte>) val).setByUser((byte) mainNewValue);
                        else if (value.getValue() instanceof Number)
                            ((ESProperty<Number>) val).setByUser(mainNewValue);
                    }
                    else
                    {
                        if (value.getValue() instanceof Integer)
                            ((Property<Integer>) value).setValue((int) mainNewValue);
                        else if (value.getValue() instanceof Float)
                            ((Property<Float>) value).setValue((float) mainNewValue);
                        else if (value.getValue() instanceof Double)
                            ((Property<Double>) value).setValue(mainNewValue);
                        else if (value.getValue() instanceof Long)
                            ((Property<Long>) value).setValue((long) mainNewValue);
                        else if (value.getValue() instanceof Short)
                            ((Property<Short>) value).setValue((short) mainNewValue);
                        else if (value.getValue() instanceof Byte)
                            ((Property<Byte>) value).setValue((byte) mainNewValue);
                        else if (value.getValue() instanceof Number)
                            ((Property<Number>) value).setValue(mainNewValue);
                    }
                    onEvent(mainNewValue);

                    event.consume();
                }
            }
        });
        c.addEventHandler(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event)
            {
                if (event.isDirect())
                    event.consume();
            }
        });
    }

    public void onRelease()
    {

    }

    public void onPress()
    {

    }

    public void onEvent(double newValue)
    {
        if (onEvent != null)
            onEvent.set(newValue);
    }
                                                                                        // 127 - stable
    public static double getMouseDivider(double min, double max)
    {
        return 1.0 / ((max-min)/185.0); // было 200
    }

    public boolean isInverted()
    {
        return inverted;
    }

    public void setInverted(boolean inverted)
    {
        this.inverted = inverted;
    }
}
