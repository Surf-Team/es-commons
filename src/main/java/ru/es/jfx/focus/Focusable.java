package ru.es.jfx.focus;

import ru.es.jfx.focus.FocusDispatcher;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import ru.es.log.Log;

/**
 * Created by saniller on 26.01.2017.
 */
public class Focusable
{
    public enum Navigate
    {
        Up,
        Down,
        Right,
        Left
    }
    public enum Grid
    {
        All,
        Start,
        End
    }

    private Node node;

    public Node getNode()
    {
        return node;
    }

    public void install(Node node, FocusDispatcher dispatcher)
    {
        this.node = node;
        node.addEventFilter(MouseEvent.MOUSE_PRESSED, e->node.requestFocus());
        node.focusedProperty().addListener((observable, oldValue, newValue) ->
        {
            Log.warning("focused changed: "+newValue);
            if (newValue)
                dispatcher.set(this);
        });
    }

    public void setFocused()
    {
        if (node != null)
            node.requestFocus();
        else
            Log.warning("Node not set for focusable. Is loading form?");
    }

    public void onEventDelete()
    {}
    public void onEventRename()
    {}
    public void onEventCopy()
    {}
    public void onEventCut()
    {
        onEventCopy();
        onEventDelete();
    }
    public void onEventPaste()
    {}
    public void onEventSelectAll()
    {}
    public void onEventDuplicate()
    {}


    public void onEventNavigate(boolean press, Navigate navigate, boolean alt, boolean ctrl, boolean shift, boolean isScroll)
    {

    }

    public void onEventGrid(Grid grid)
    {}

    public void onEventLegato()
    { }


}
