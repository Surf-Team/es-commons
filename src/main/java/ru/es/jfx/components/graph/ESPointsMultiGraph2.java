package ru.es.jfx.components.graph;

import ru.es.util.SortUtils;
import ru.es.jfx.graph.GraphLine;
import ru.es.jfx.graph.GraphLinesContainer;
import ru.es.jfx.canvas.IPaintFunction;
import ru.es.jfx.canvas.IUpdatingUI;
import ru.es.jfx.canvas.PaintingPane;
import ru.es.util.TimeUtils;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Map;

public class ESPointsMultiGraph2 extends PaintingPane
{
    IUpdatingUI layer1;
    public Color backgroundColor = Color.rgb(82, 82, 82);

    final GraphLinesContainer container;

    public ESPointsMultiGraph2(GraphLinesContainer container)
    {
        this.container = container;

        layer1 = createLayer(new IPaintFunction() {
            @Override
            public void paintComponent(GraphicsContext gr, double width, double height)
            {
                gr.setFill(backgroundColor);
                gr.fillRect(0,0,width,height);
                // 100 pix = 1000 ms
                // 1 pix = 10 ms
                int i = 0;
                ArrayList<Long> timeLines = new ArrayList<>();
                for (Map.Entry<String, GraphLine> map : container.graphs.entrySet())
                {
                    gr.setFill(graphColor[i % graphColor.length]);
                    gr.setStroke(gr.getFill());

                    GraphLine m = map.getValue();
                    i++;
                    double oldX = Double.MIN_VALUE;
                    double oldY = Double.MIN_VALUE;
                    long lastTime = 0;
                    double value = 0;

                    for (long time : SortUtils.sortLongs(m.timeStats.keySet()))
                    {
                        if (time < getMinShowMillis())
                            continue;

                        value = m.timeStats.get(time);

                        double x = (getWidth() - ((System.currentTimeMillis() - time) / getMillisInOnePixel()));
                        double y = ((getMaxValue() - value) / getValueInOnePixel());

                        if (oldX != Double.MIN_VALUE)
                        {
                            gr.strokeLine(x, y, oldX, oldY);
                        }
                        oldX = x;
                        oldY = y;

                        gr.fillArc(x, y, arcSize, arcSize, 0, 360, ArcType.ROUND);

                        lastTime = time;

                        if (!timeLines.contains(time))
                        {
                            timeLines.add(time);
                        }
                    }

                    gr.setTextAlign(TextAlignment.RIGHT);
                    gr.setTextBaseline(VPos.BOTTOM);
                    gr.fillText(m.name+": "+value, oldX, oldY);

                    gr.setTextAlign(TextAlignment.LEFT);
                    gr.setTextBaseline(VPos.TOP);
                    gr.fillText(map.getKey()+": "+value, 5, 12 + i*12);
                }

                gr.setStroke(Color.rgb(0,0,0,0.5));
                gr.setFill(Color.BLACK);
                for (int k = 0; k <= 4; k++)
                {
                    double part = k / 4.0;
                    double value = getMaxValue() * part;
                    double y = ((getMaxValue() - value) / getValueInOnePixel());
                    gr.strokeLine(0, y, width, y);
                    gr.fillText(""+value, width/2.0, y);
                }

                gr.setFill(Color.BLACK);
                for (long time : timeLines)
                {
                    double x = (getWidth() - ((System.currentTimeMillis() - time) / getMillisInOnePixel()));
                    gr.fillText( TimeUtils.getDateString(time), x, 20);
                }
            }
        });
    }

    private double getMinValue()
    {
        double min = Double.MAX_VALUE;
        for (GraphLine m : container.graphs.values())
        {
            for (Map.Entry<Long, Double> entry : m.timeStats.entrySet())
            {
                if (entry.getKey() < getMinShowMillis())
                    continue;

                if (entry.getValue() < min)
                    min = entry.getValue();
            }
        }
        if (min == Double.MAX_VALUE)
            min = 0;
        return min;
    }

    private double getMaxValue()
    {
        double max = Double.MIN_VALUE;
        for (GraphLine m : container.graphs.values())
        {
            for (Map.Entry<Long, Double> entry : m.timeStats.entrySet())
            {
                if (entry.getKey() < getMinShowMillis())
                    continue;

                if (entry.getValue() > max)
                    max = entry.getValue();
            }
        }
        if (max == Double.MIN_VALUE)
            max = 100;
        return max;
    }

    private double getMillisInOnePixel()
    {
        return 10*60*1000;
    }

    private double getValueInOnePixel()
    {
        double ret = (getMaxValue() - getMinValue()) / getHeight();
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


    public int getLastValue(String name)
    {
        return 1;
    }
}
