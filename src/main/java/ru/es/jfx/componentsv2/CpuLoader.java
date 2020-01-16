package ru.es.jfx.componentsv2;

import com.sun.management.OperatingSystemMXBean;
import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.fonts.ESFonts;
import ru.es.jfx.canvas.IPaintFunction;
import ru.es.jfx.canvas.IUpdatingUI;
import ru.es.jfx.canvas.PaintingPane;
import ru.es.log.Log;
import ru.es.math.ESMath;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.lang.management.ManagementFactory;
import java.util.LinkedList;

public class CpuLoader extends PaintingPane
{
    private LinkedList<Float> list = new LinkedList<>();
    private int size = 60;
    private String lastCpuLoad;
    private float lastCpu = 0;
    IUpdatingUI updater;

    public ESProperty<Boolean> allowUpdateCPULoad = new ESProperty<>(true);

    public CpuLoader()
    {
        setMinWidth(60);
        setMaxWidth(60);
        setPrefWidth(60);
        setMinHeight(20);

        for (int i = 0; i < 60; i++)
            list.add(0f);

        updater = createLayer(new IPaintFunction() {
            @Override
            public void paintComponent(GraphicsContext gr, double width, double height)
            {
                if (error)
                    return;

                gr.clearRect(0,0,width,height);
                //gr.setFill(Color.BLACK);
                //gr.fillRect(0,0,width,height);

                gr.setTextAlign(TextAlignment.CENTER);

                gr.setFill(Color.DIMGRAY);

                for (int i = 0; i < size; i++)
                {
                    double vol = list.get(i);

                    if (vol < 0.50)
                        gr.setFill(Color.rgb(0,0,0,0.8));
                    else if (vol < 0.90)
                        gr.setFill(Color.rgb(255,187,187));
                    else
                        gr.setFill(Color.rgb(255,0,0));

                    gr.fillRect(width-i, height - vol*height,  1, vol*height);
                }

                gr.setFill(Color.LIGHTGRAY);


                gr.setFont(ESFonts.simpleFont);
                gr.fillText(lastCpuLoad, width/2, height/2+6);
            }
        });
    }


    boolean error = false;

    public void addRecord()
    {
        if (!allowUpdateCPULoad.get())
            return;
        
        try
        {
            OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            addCpuInfo((float) ESMath.min(1.0f, operatingSystemMXBean.getProcessCpuLoad() * 1.5));
            updater.updateUI();
        }
        catch (Throwable e)
        {
            if (!error)
            {
                Log.warning("CHECKING 5");
                Log.warning("ERROR. CPU MONITOR COULDN'T BE LOAD");
                e.printStackTrace();
                e.printStackTrace();
                error = true;
            }
        }
    }


    private void addCpuInfo(float d)
    {
        list.remove(59);
        list.add(0, d);
        lastCpuLoad = ((int) (d * 100)) + "%";
        lastCpu = d;
    }


    int clockCycle = 0;

    public void clock40()
    {
        clockCycle++;
        if (clockCycle % 15 == 0)
        {
            addRecord();
        }
    }
}
