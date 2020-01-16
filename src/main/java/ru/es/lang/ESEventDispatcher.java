package ru.es.lang;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;
import ru.es.models.ESArrayList;
import ru.es.thread.ESThreadPoolManager;
import ru.es.thread.RunnableImpl;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.EventListener;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by saniller on 02.12.2016.
 */
public class ESEventDispatcher extends RunnableImpl implements Observable, EventListener, InvalidationListener
{
    public boolean isSelfRunLater = false;
    public String debugName = null;

    public ESArrayList<Runnable> onEvent;
    public ESArrayList<InvalidationListener> onEventInv;
    private ESArrayList<Runnable> onEventOnce;
    List<Runnable> runnableList;

    public ESEventDispatcher()
    {

    }

    public ESEventDispatcher(Observable... observables)
    {
        for (Observable o : observables)
            o.addListener(this);
    }


    public void addOnEvent(Runnable r)
    {
        if (onEvent == null)
            onEvent = new ESArrayList<>();

        onEvent.add(r);
        if (debugName != null)
        {
            Log.warning(debugName + ": addOnEvent new runnable. Total size: " + onEvent.size());
        }
    }

    public void addOnEventOnce(Runnable r)
    {
        if (onEventOnce == null)
            onEventOnce = new ESArrayList<>();

        onEventOnce.add(r);

        if (debugName != null)
            Log.warning(debugName+": onEventOnce new runnable. Total size once: "+onEventOnce.size());
    }

    public void removeOnEvent(Runnable r)
    {
        if (onEvent != null)
            onEvent.remove(r);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void event()
    {
        if (debugName != null)
            Log.warning(debugName+": event. Total onEvent size / onEventOnce: "+onEvent.size()+" / "+onEventOnce.size());

        if (onEvent != null)
        {
            for (Runnable r : onEvent)
            {
                r.run();
            }
        }
        if (onEventInv != null)
        {
            for (InvalidationListener r : onEventInv)
            {
                r.invalidated(this);
            }
        }

        if (onEventOnce != null)
        {
            if (runnableList == null)
                runnableList = new ESArrayList<>();
            else
                runnableList.clear();

            for (Runnable r : onEventOnce)
            {
                r.run();
                runnableList.add(r);
            }
            onEventOnce.removeAll(runnableList);
        }
    }

    ScheduledFuture eventLater;
    ScheduledFuture eventLater2;

    public void eventLater()
    {
        if (eventLater == null)
            eventLater = ESThreadPoolManager.getInstance().scheduleGeneral(new RunnableImpl()
            {
                @Override
                public void runImpl() throws Exception
                {
                    eventLater = null;
                    event();
                }
            }, 30);
    }

    public void eventLater500NotSafe()
    {
        //ESThreadPoolManager.getInstance().addObjectLimitedTask200(this, this);
        if (eventLater2 == null)
            eventLater2 = ESThreadPoolManager.getInstance().scheduleGeneral(new RunnableImpl()
            {
                @Override
                public void runImpl() throws Exception
                {
                    event();
                    eventLater2 = null;
                }
            }, 60);
    }

    @Override
    public void runImpl() throws Exception
    {
        if (isSelfRunLater)
        {
            eventLater();
        }
        else
            event();
    }

    @Override
    public void addListener(InvalidationListener listener)
    {
        if (onEventInv == null)
            onEventInv = new ESArrayList<>();

        onEventInv.add(listener);
    }

    @Override
    public void removeListener(InvalidationListener listener)
    {
        if (onEventInv != null)
            onEventInv.remove(listener);
    }

    @Override
    public void invalidated(Observable observable)
    {
        event();
    }
}
