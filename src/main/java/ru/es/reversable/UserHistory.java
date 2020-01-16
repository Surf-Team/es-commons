package ru.es.reversable;

import ru.es.log.Log;

import java.util.LinkedList;

/**
 * Created by saniller on 27.03.2017.
 */
public class UserHistory
{
    public UserHistory()
    {

    }

    LinkedList<IReversableFunction> history = new LinkedList<>();
    IReversableFunction lastFunction = null;
    boolean wasUndo = false;

    public synchronized void useFunction(IReversableFunction u)
    {
        if (u == null)
            return;
        
        // если отменяли действие только что - удаляем всё что после
        if (wasUndo)
        {
            int maxIndex = history.indexOf(lastFunction);

            while (history.size() > maxIndex+1)
                history.pollLast();
        }

        history.offer(u);
        u.doFunction();
        while (history.size() > 20)
            history.poll();

        wasUndo = false;
        lastFunction = u;
    }

    public void undo()
    {
        if (lastFunction == null)
            return;

        lastFunction.undoFunction();
        wasUndo = true;

        int newIndex = history.indexOf(lastFunction) - 1;
        Log.warning("UserHistory: Next undo newIndex: "+newIndex+", size: "+history.size()+", makeUndoToIndex: "+history.indexOf(lastFunction));
        if (newIndex < 0)
        {
            lastFunction = null;
        }
        else
            lastFunction = history.get(newIndex);
    }

    public void redo()
    {
        int newIndex = 0;

        if (lastFunction != null)
            newIndex = history.indexOf(lastFunction)+1;

        if (newIndex >= history.size())
            return; // cant redo more

        lastFunction = history.get(newIndex);

        //lastFunction.doFunction();
        lastFunction.redoFunction();
    }

    /*ReversableMouseEvent defaultEvent = new ReversableMouseEvent(false, true);

    public void eventDefault(ESXmlObject xmlObject)
    {
        defaultEvent.eventSimple(xmlObject);
    }

    public void eventDefault(List<ESXmlObject> xmlObject)
    {
        defaultEvent.eventSimple(xmlObject);
    } */
}
