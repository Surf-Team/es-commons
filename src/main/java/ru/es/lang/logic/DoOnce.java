package ru.es.lang.logic;


public class DoOnce
{
    private boolean done = false;

    public DoOnce()
    {

    }

    private Runnable r;

    public DoOnce(Runnable r)
    {
        this.r = r;
    }

    public void doOnce()
    {
        if (r != null && canDo())
            r.run();

    }

    public boolean canDo()
    {
        if (done)
            return false;

        done = true;
        return true;
    }

    public void reinstall()
    {
        done = false;
    }

}
