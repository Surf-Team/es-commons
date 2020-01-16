package ru.es.jfx.focus;

/**
 * Created by saniller on 22.12.2016.
 */
public class FocusableDragable extends Focusable
{
    public final Indexed indexed;

    public FocusableDragable(Indexed indexed)
    {
        this.indexed = indexed;
    }

    public boolean allowDrag()
    {
        return false;
    }

    public final Indexed getIndexed()
    {
        return indexed;
    }

    @Override
    public void onEventNavigate(boolean press, Navigate navigate, boolean alt, boolean ctrl, boolean shift, boolean isScroll)
    {
        if (alt || ctrl || shift)
        {
            if (press)
            {
                if (navigate == Navigate.Up)
                    moveUp();
                else if (navigate == Navigate.Down)
                    moveDown();
            }
        }
    }

    public void moveUp()
    {
        if (getIndexed() != null)
            getIndexed().getMovingEvent().moveUp(getIndexed());
    }

    public void moveDown()
    {
        if (getIndexed() != null)
            getIndexed().getMovingEvent().moveDown(getIndexed());
    }
}
