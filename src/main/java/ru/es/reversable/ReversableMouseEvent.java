package ru.es.reversable;


import ru.es.jfx.xml.IXmlObject;
import ru.es.log.Log;
import ru.es.thread.ESThreadPoolManager;
import ru.es.thread.RunnableImpl;
import ru.es.util.ListUtils;
import javafx.scene.input.MouseEvent;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by saniller on 17.04.2017.
 */
public class ReversableMouseEvent
{
    boolean mustAddToHistory = false;
    boolean alwaysAddToHistory;

    ScheduledFuture historySaveFuture;
    long lastHistoryEvent = 0;
    IReversableFunction historyReversableFunc;

    boolean updateOnDrag = false;
    public ReversableMouseEvent(boolean updateOnDrag, boolean alwaysAddToHistory)
    {
        this.updateOnDrag = updateOnDrag;
        this.alwaysAddToHistory = alwaysAddToHistory;
    }

    public void eventSimple(UserHistory userHistory, MouseEvent event, List<IXmlObject> savedObjects)
    {
        boolean isRelease = event.getEventType() == javafx.scene.input.MouseEvent.MOUSE_RELEASED;
        boolean isDrag = event.getEventType() == javafx.scene.input.MouseEvent.MOUSE_DRAGGED;
        boolean isPress = event.getEventType() == javafx.scene.input.MouseEvent.MOUSE_PRESSED;

        if (isPress)
            mustAddToHistory = false;

        if (isPress || isRelease || (updateOnDrag && isDrag))
        {
            eventSimple(userHistory, savedObjects);
        }
    }

    public void eventSimple(UserHistory userHistory, IXmlObject savedObjects)
    {
        eventSimple(userHistory, ListUtils.createList(savedObjects));
    }

    public void eventSimple(UserHistory userHistory, List<IXmlObject> savedObjects)
    {
        lastHistoryEvent = System.currentTimeMillis();
        if (historySaveFuture == null)
        {
            historyReversableFunc = ReversableUtils.makeForXML(savedObjects);
            historySaveFuture = ESThreadPoolManager.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl()
            {
                @Override
                public void runImpl() throws Exception
                {
                    if (lastHistoryEvent + 300 < System.currentTimeMillis())
                    {
                        boolean addToHistory = false;
                        if (mustAddToHistory || alwaysAddToHistory)
                        {
                            userHistory.useFunction(historyReversableFunc);
                            addToHistory = true;
                        }

                        Log.warning("Function used with delay. Add to history: " + addToHistory);
                        historySaveFuture.cancel(false);
                        historySaveFuture = null;
                    }
                }
            }, 100, 100);
        }
    }

    public void needToBeAddToHistory()
    {
        mustAddToHistory = true;
    }
}
