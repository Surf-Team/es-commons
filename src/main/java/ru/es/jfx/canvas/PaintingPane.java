package ru.es.jfx.canvas;

import ru.es.jfx.shortcut.MassChangeListener;
import ru.es.thread.ESThreadPoolManager;
import ru.es.thread.RunnableImpl;
import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;


/**
 * Created by saniller on 13.03.2017.
 */
public class PaintingPane extends StackPane
{
    // createLayer - добавляет новый слой
    // размер слоя автоматически подстраивается под размер текущего компонента
    // слои не меняют размер компонента
    public IUpdatingUI createLayer(IPaintFunction painFunction, Observable... redrawOn)
    {
        Canvas newCanvas = new Canvas();
        //newCanvas.setMouseTransparent(true);
        newCanvas.setManaged(false);

        RunnableImpl r = new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                painFunction.paintComponent(newCanvas.getGraphicsContext2D(), getWidth(), getHeight());
            }
        };

        IUpdatingUI ret = new IUpdatingUI() {
            @Override
            public void updateUI()
            {
                ESThreadPoolManager.getInstance().addGUITask(newCanvas, r);
            }

            @Override
            public Canvas getCanvas()
            {
                return newCanvas;
            }
        };


        new MassChangeListener(widthProperty(), heightProperty())
        {
            @Override
            public void changed()
            {
                newCanvas.setWidth(getWidth());
                newCanvas.setHeight(getHeight());
                ret.updateUI();
            }
        };

        this.getChildren().add(newCanvas);


        new MassChangeListener(redrawOn)
        {
            @Override
            public void changed()
            {
                ret.updateUI();
            }
        };

        return ret;
    }

}
