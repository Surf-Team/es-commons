package ru.es.jfx.focus;


public class FocusDispatcher implements IFocusContainer
{
    private Focusable focusable;
    
    public void set(Focusable focusable)
    {
        this.focusable = focusable;
    }

    public Focusable get()
    {
        return focusable;
    }
    
    public void onEventCopy()
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventCopy();
    }

    public void onEventPaste()
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventPaste();
    }
    public void onEventDelete()
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventDelete();
    }
    public void onEventRename()
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventRename();
    }
    public void onEventSelectAll()
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventSelectAll();
    }
    public void onEventDuplicate()
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventDuplicate();
    }

    public void onEventNavigate(boolean press, Focusable.Navigate navigate, boolean alt, boolean ctrl, boolean shift, boolean isScroll)
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventNavigate(press, navigate, alt, ctrl, shift, isScroll);
    }

    public void onEventCut()
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventCut();
    }

    public void onEventLegato()
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventLegato();
    }

    public void onEventGrid(Focusable.Grid grid)
    {
        if (focusable != null && focusable.getNode().isFocused())
            focusable.onEventGrid(grid);
    }
}
