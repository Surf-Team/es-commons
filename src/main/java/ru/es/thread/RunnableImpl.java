package ru.es.thread;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 11.01.15
 * Time: 23:45
 * To change this template use File | Settings | File Templates.
 */
public abstract class RunnableImpl implements Runnable
{
    public abstract void runImpl() throws Exception;

    @Override
    public final void run()
    {
        try
        {
            runImpl();
        }
        catch(Exception e)
        {
            catchException(e);
        }
    }

    public void catchException(Exception e)
    {
        e.printStackTrace();
    }
}
