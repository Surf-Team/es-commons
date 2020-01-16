package ru.es.jfx.components;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;

/**
 * Created by saniller on 06.09.2017.
 */
@Deprecated
public class ESScrollPane extends ScrollPane
{
    public ESScrollPane()
    {
        setCache(false);

        skinProperty().addListener(new ChangeListener<Skin<?>>() {
            @Override
            public void changed(ObservableValue<? extends Skin<?>> observable, Skin<?> oldValue, Skin<?> newValue)
            {
                try
                {
                    for (Object o : ((SkinBase) newValue).getChildren())
                    {
                        Node n = (Node) o;
                        n.setCache(false);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
