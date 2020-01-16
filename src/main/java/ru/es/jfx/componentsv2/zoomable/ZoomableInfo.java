package ru.es.jfx.componentsv2.zoomable;

import javafx.beans.property.SimpleDoubleProperty;
import ru.es.jfx.binding.ESProperty;
import ru.es.jfx.componentsv2.scrollpane.PFScrollPane;
import ru.es.lang.ESEventDispatcher;
import ru.es.log.Log;
import ru.es.util.ESFXUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.Property;

public class ZoomableInfo
{
    public final SimpleDoubleProperty value = new SimpleDoubleProperty(0.0); // == start visible unit
    public final SimpleDoubleProperty visibleSize = new SimpleDoubleProperty(1.0);
    public final SimpleDoubleProperty min = new SimpleDoubleProperty(0.0);
    public final SimpleDoubleProperty max = new SimpleDoubleProperty( 1.0);
    public final SimpleDoubleProperty minZoom = new SimpleDoubleProperty( 0.1);
    public final Property<Boolean> zoomEnabled = new ESProperty<>(true);

    public final ESEventDispatcher somethingChanged = new ESEventDispatcher();

    public ZoomableInfo()
    {
        ESFXUtils.massChangeListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable)
            {
                somethingChanged.event();
            }
        }, value, visibleSize, min, max, minZoom, zoomEnabled);
    }

    public void bindTo(ZoomableInfo other)
    {
        value.bindBidirectional(other.value);
        visibleSize.bindBidirectional(other.visibleSize);
        min.bindBidirectional(other.min);
        max.bindBidirectional(other.max);
        minZoom.bindBidirectional(other.minZoom);
        zoomEnabled.bindBidirectional(other.zoomEnabled);
    }

    public static ZoomableInfo createFromScrollPane(PFScrollPane sp, boolean horizontal)
    {
        ZoomableInfo ret = new ZoomableInfo();

        ret.value.bindBidirectional(horizontal ? sp.hvalueProperty() : sp.vvalueProperty());
        ret.visibleSize.setValue(1);
        ret.min.bindBidirectional(horizontal ? sp.hminProperty() : sp.vminProperty());
        ret.max.bindBidirectional(horizontal ? sp.hmaxProperty() : sp.vmaxProperty());
        ret.zoomEnabled.setValue(false);

        return ret;
    }

    public void setMax(Number n)
    {
        max.setValue(n);
    }

    public void setVisibleSize(Number n)
    {
        visibleSize.setValue(n);
    }

    public void setValue(Number n)
    {
        value.setValue(n);
    }

    public void seeAll()
    {
        if (zoomEnabled.getValue())
        {
            value.setValue(0.0);
            Log.warning("See all. Full size: "+getFullSize());
            visibleSize.setValue(getFullSize());
        }
    }

    public double getFullSize()
    {
        return max.get() - min.get();
    }

    public void setMin(Number n)
    {
        min.setValue(n);
    }
}
