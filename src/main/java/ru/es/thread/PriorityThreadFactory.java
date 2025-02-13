package ru.es.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class PriorityThreadFactory implements ThreadFactory
{
    private int _prio;
    private String _name;
    private AtomicInteger _threadNumber = new AtomicInteger(1);
    private ThreadGroup _group;
    private boolean _daemon;

    public PriorityThreadFactory(String name, int prio, boolean daemon)
    {
        _prio = prio;
        _name = name;
        _group = new ThreadGroup(_name);
        _daemon = daemon;
    }

    /* (non-Javadoc)
     * @see java.util.concurrent.ThreadFactory#newThread(java.lang.Runnable)
     */
    public Thread newThread(Runnable r)
    {
        Thread t = new Thread(_group, r);
        t.setName(_name + "-" + _threadNumber.getAndIncrement());
        t.setPriority(_prio);
        t.setDaemon(_daemon);
        return t;
    }

    public ThreadGroup getGroup()
    {
        return _group;
    }
}
