package ru.es.jfx.componentsv2;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.jfx.canvas.IPaintFunction;
import ru.es.jfx.canvas.PaintingPane;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PFShadow extends PaintingPane
{
    public ObjectProperty<Side> sideProperty = new SimpleObjectProperty<>();
    public DoubleProperty base = new SimpleDoubleProperty(0.25);
    public DoubleProperty decrement = new SimpleDoubleProperty(0.02);

    public void setSide(Side side)
    {
        this.sideProperty.set(side);
    }

    public Side getSide()
    {
        return this.sideProperty.get();
    }

    public PFShadow()
    {
        createLayer(new IPaintFunction() {
            @Override
            public void paintComponent(GraphicsContext gr, double width, double height)
            {
                gr.clearRect(0,0,width,height);
                drawShadow(gr, sideProperty.get(), base.get(), decrement.get());
            }
        }, sideProperty, base, decrement);
    }

    // base = 0.25, decrement = 0.02
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static void drawShadow(GraphicsContext gr, Side side, double base, double decrement)
    {
        double height = gr.getCanvas().getHeight();
        double width = gr.getCanvas().getWidth();

        double x = 0;
        double y = 0;
        double opacity = base;

        if (side == Side.BOTTOM)
            y = height;
        else if (side == Side.RIGHT)
            x = width;

        while (opacity > 0)
        {
            opacity -= decrement;

            if (opacity < 0)
                break;

            gr.setFill(Color.grayRgb(0, opacity));

            if (side == Side.LEFT)
            {
                gr.fillRect(x, y, 1, height);
                x++;
            }
            else if (side == Side.RIGHT)
            {
                gr.fillRect(x, y, 1, height);
                x--;
            }
            else if (side == Side.TOP)
            {
                gr.fillRect(x, y, width, 1);
                y++;
            }
            else if (side == Side.BOTTOM)
            {
                gr.fillRect(x, y, width, 1);
                y--;
            }
        }
    }

}
