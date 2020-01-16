package ru.es.jfx.components.graph;

import ru.es.jfx.canvas.IPaintFunction;
import ru.es.jfx.canvas.IUpdatingUI;
import ru.es.jfx.canvas.PaintingPane;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

import java.util.Map;

public abstract class ESPointsMultiGraphs extends PaintingPane
{
    IUpdatingUI layer1;
    public Color backgroundColor = Color.rgb(82, 82, 82);

    public ESPointsMultiGraphs()
    {
        layer1 = createLayer(new IPaintFunction() {
            @Override
            public void paintComponent(GraphicsContext gr, double width, double height)
            {
                gr.setFill(backgroundColor);
                gr.fillRect(0,0,width,height);
                // 100 pix = 1000 ms
                // 1 pix = 10 ms
                int i = 0;
                for (Map.Entry<String, Map<Long, Integer>> map : getData().entrySet())
                {
                    gr.setFill(graphColor[i % graphColor.length]);
                    gr.fillText(map.getKey()+": "+getLastValue(map.getKey()), 5, 12 + i*12);

                    Map<Long, Integer> m = map.getValue();
                    i++;
                    for (Map.Entry<Long, Integer> entry : m.entrySet())
                    {
                        if (entry == null)
                            continue;

                        if (entry.getKey() == null)
                            continue;

                        if (entry.getKey() < getMinShowMillis())
                            continue;

                        int x = (int) (getWidth() - ((System.currentTimeMillis() - entry.getKey()) / getMillisInOnePixel()));
                        int y = (int) ((getMaxValue() - entry.getValue()) / getValueInOnePixel());

                        gr.fillArc(x, y, arcSize, arcSize, 0, 360, ArcType.ROUND);
                    }
                }
            }
        });
    }

    private int getMinValue()
    {
        int min = Integer.MAX_VALUE;
        for (Map<Long, Integer> m : getData().values())
        {
            for (Map.Entry<Long, Integer> entry : m.entrySet())
            {
                if (entry.getKey() < getMinShowMillis())
                    continue;

                if (entry.getValue() < min)
                    min = entry.getValue();
            }
        }
        if (min == Integer.MAX_VALUE)
            min = 0;
        return min;
    }

    private int getMaxValue()
    {
        int max = Integer.MIN_VALUE;
        for (Map<Long, Integer> m : getData().values())
        {
            for (Map.Entry<Long, Integer> entry : m.entrySet())
            {
                if (entry.getKey() < getMinShowMillis())
                    continue;

                if (entry.getValue() > max)
                    max = entry.getValue();
            }
        }
        if (max == Integer.MIN_VALUE)
            max = 100;
        return max;
    }

    private int getMillisInOnePixel()
    {
        //return millsInPixelSlider.getValue();
        return 1700;
    }

    private double getValueInOnePixel()
    {
        double ret = (double) (getMaxValue() - getMinValue()) / (double) getHeight();
        return ret;
    }

    private long getMinShowMillis()
    {
        return (System.currentTimeMillis() - (long) (getWidth() * getMillisInOnePixel()));
    }

    public Color[] graphColor = new Color[]
            {
                    Color.rgb(244, 134, 49),
                    Color.rgb(244, 232, 0),
                    Color.rgb(0, 209, 244),
                    Color.rgb(206, 82, 255),
                    Color.rgb(244, 0, 142),
                    Color.rgb(0, 150, 244),
                    Color.rgb(244, 118, 118),
                    Color.rgb(244, 147, 242),
                    Color.rgb(171, 108, 244),
                    Color.rgb(29, 244, 168),
                    Color.rgb(198, 244, 0),
                    Color.rgb(12, 0, 244),
                    Color.rgb(244, 205, 205)
            } ;
    public int arcSize = 3;

    public void updateUI()
    {
        layer1.updateUI();
    }

    public abstract Map<String, Map<Long, Integer>> getData();

    public abstract Integer getLastValue(String name);
}
