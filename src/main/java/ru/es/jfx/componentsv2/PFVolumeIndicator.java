package ru.es.jfx.componentsv2;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.audio.FloatGetter;
import ru.es.audio.basics.VolumeRegulatorSetting;
import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.canvas.IPaintFunction;
import ru.es.jfx.canvas.IUpdatingUI;
import ru.es.jfx.canvas.PaintingPane;
import ru.es.jfx.ESColors;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PFVolumeIndicator extends PaintingPane
{
    public enum Style
    {
        BottomToTop,
        LeftToRight
    }

    public ESProperty<FloatGetter> value = new ESProperty<>(()->0.6f); // volume
    public ESProperty<FloatGetter> valueBack = new ESProperty<>(()->0.7f); // volume middle
    public ESProperty<Style> controlStyle = new ESProperty<>(Style.BottomToTop);
    public ESProperty<Boolean> reverceMode = new ESProperty<>(false); // for compressors
    public ESProperty<Double> maxAmp = new ESProperty<>(2.0);

    public VolumeRegulatorSetting setting = VolumeRegulatorSetting.LowInfiniteUp6;

    int stereoIndex;
    public boolean showIndex0IfMono = false; // если текущий индекс = 1 (правый) и вход = null, то берём данные из индекса 0
    private IUpdatingUI updater;

    public PFVolumeIndicator()
    {
        updater = createLayer(new IPaintFunction() {
            @Override
            public void paintComponent(GraphicsContext gr, double width, double height)
            {
                update(gr, width, height);
            }
        }, controlStyle, reverceMode, value, valueBack);


        updater.updateUI();
    }

    /* Не добавляем, т.к. иначе приватную аудио библиотеку нужно тянуть
    public void setAnalyzer(BufferVolumeAnalizerProperty analizer)
    {
        if (showIndex0IfMono && analizer != null)
        {
            if (analizer.lastInputIsNull[1])
            {
                valueBack.set(analizer.maxAmpIn1Sec[0]);
                value.set(analizer.maxAmp[0]);
            }
            else
            {
                value.set(analizer.maxAmp[stereoIndex]);
                valueBack.set(analizer.maxAmpIn1Sec[stereoIndex]);
            }
        }
    } */


    float lastVol = -1;
    double lastHeight = 0;
    double lastWidth = 0;
    float lastPointerSize = Integer.MIN_VALUE;
    float minAmp = 0.0f;
    float last1SecVol = 0;
    public Color accentColor = Color.rgb(150,235,0);

    Color vol2Color = Color.web("#FF5a00");

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    private void update(GraphicsContext g, double width, double height)
    {
        if (controlStyle.get() == Style.LeftToRight)
            vol2Color = accentColor;


        float vol = value.get().value();


        if (vol == lastVol && lastHeight == getHeight() && lastWidth == getWidth() &&
                (valueBack == null || valueBack.get().value() == last1SecVol))
            return;

        boolean sizeChanged = false;
        if (lastHeight != getHeight() || lastWidth != getWidth())
            sizeChanged = true;

        lastHeight = height;
        lastWidth = width;

        if (valueBack != null)
            last1SecVol = valueBack.get().value();

        lastVol = vol;


        g.setFill(Color.BLACK);
        g.fillRect(0, 0, width, height);


        int cycleStart = 1;
        int cycleEnd = 1;

        if (valueBack != null)
            cycleStart = 0;

        float maxAmp = this.maxAmp.get().floatValue();

        for (int cycle = cycleStart; cycle <= cycleEnd; cycle++)
        {
            if (cycle == 1)
            {
                vol = value.get().value();
                if (!reverceMode.get())
                {
                    if (vol != 0)
                    {
                        if (vol <= 1)
                            g.setFill(accentColor);
                        else
                            g.setFill(ESColors.volumeLimit);
                    }
                }
                else
                    g.setFill(accentColor);
            }
            else if (cycle == 0)
            {
                g.setFill(vol2Color);
                vol = last1SecVol;
            }

            float pointerSize;


            double normalizedVal =  setting.ampToNormalized(vol);

            if (controlStyle.get() == Style.BottomToTop)
            {
                pointerSize = (float) (normalizedVal * height);
            }
            else
            {
                pointerSize = (float) (normalizedVal * width);
            }

            //if (!sizeChanged && Math.abs(lastPointerSize - pointerSize) <= 1)
            //return;

            lastPointerSize = pointerSize;
            double h = pointerSize;

            if (cycle == 0)
            {
                h = 2;
            }

            if (!reverceMode.get())
            {
                if (vol != 0)
                {
                    if (controlStyle.get() == Style.BottomToTop)
                    {
                        g.fillRect(0, height - pointerSize, width, h);
                    }
                    else
                    {
                        if (cycle == 0)
                            g.fillRect(pointerSize, 0, 2, height);
                        else
                            g.fillRect(0, 0, pointerSize, height);
                    }
                }
            }
            else
            {
                double zeroVal = setting.ampToNormalized(vol);

                double pointerZero;

                if (controlStyle.get() == Style.BottomToTop)
                {
                    pointerZero = height - zeroVal * height;
                }
                else
                {
                    pointerZero = width - zeroVal * width;
                }

                if (controlStyle.get() == Style.BottomToTop)
                {
                    g.fillRect(0, pointerZero, width, height - pointerZero - pointerSize);
                }
                else
                {
                    g.fillRect(pointerSize, 0, width - pointerZero - pointerSize, height);
                }
            }
        }
    }


    public void update()
    {
        if (getScene() == null)
            return;

        updater.updateUI();
    }

    public void setControlStyle(Style controlStyle)
    {
        this.controlStyle.set(controlStyle);
    }

    public Style getControlStyle()
    {
        return this.controlStyle.get();
    }


}
