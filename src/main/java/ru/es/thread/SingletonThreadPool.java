package ru.es.thread;

import io.prometheus.client.Gauge;
import ru.es.log.Log;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by saniller on 10.07.2015.
 */
public class SingletonThreadPool
{
    private static SingletonThreadPool instance;


    private final Gauge gauge_executorQuue;
    private final Gauge gauge_scheduledExecutorQueue;

    public static SingletonThreadPool getInstance()
    {
        if(instance == null)
            instance = new SingletonThreadPool();

        return instance;
    }

    private ScheduledThreadPoolExecutor schedudledExecutor;
    private ThreadPoolExecutor executor;
    private ThreadPoolExecutor executorLow;

    private SingletonThreadPool()
    {
        schedudledExecutor = new ScheduledThreadPoolExecutor(2, new PriorityThreadFactory(
                "SingletonScheduled", Thread.NORM_PRIORITY, false));
        executor = new ThreadPoolExecutor(2, Integer.MAX_VALUE, 5L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new PriorityThreadFactory("SingletonExecutor",
                Thread.NORM_PRIORITY, false));
        executorLow = new ThreadPoolExecutor(1, 1, 5L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new PriorityThreadFactory("SingletonExecutorLow",
                Thread.MIN_PRIORITY, false));

        String poolName = "SingletonTP";
        gauge_executorQuue = Gauge.build().name("executorQueue_"+poolName).help("Executor queue for "+poolName).register();
        gauge_scheduledExecutorQueue = Gauge.build().name("scheduledExecutorQueue_"+poolName).help("Scheduled executor queue for "+poolName).register();


        Thread t = new Thread(() -> {

            while (true)
            {
                gauge_executorQuue.set(schedudledExecutor.getQueue().size());
                gauge_scheduledExecutorQueue.set(executor.getQueue().size());
                try
                {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }

		}, "SingletonService");
        t.start();

    }

    public void executeTask(RunnableImpl r)
    {
        executor.execute(r);
    }

    public void execute(Runnable r)
    {
        executor.execute(r);
    }

    @Deprecated
    public void executeLow(RunnableImpl r)
    {
        executorLow.execute(r);
    }

    public ScheduledFuture<?> scheduleGeneral(RunnableImpl r, long delay)
    {
        try
        {
            if(delay < 0)
            {
                delay = 0;
            }

            return schedudledExecutor.schedule(r, delay, TimeUnit.MILLISECONDS);
        }
        catch(Exception e)
        {
            //shutdown, ignore
            e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture<?> scheduleGeneralAtFixedRate(RunnableImpl r, long initial, long delay)
    {
        try
        {
            if(delay < 0)
            {
                delay = 0;
            }

            if(initial < 0)
            {
                initial = 0;
            }

            return schedudledExecutor.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
        }
        catch(RejectedExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture<?> scheduleWithFixedDelay(RunnableImpl r, long initial, long delay)
    {
        try
        {
            if(delay < 0)
            {
                delay = 0;
            }

            if(initial < 0)
            {
                initial = 0;
            }

            return schedudledExecutor.scheduleWithFixedDelay(r, initial, delay, TimeUnit.MILLISECONDS);
        }
        catch(RejectedExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    private class PriorityThreadFactory implements ThreadFactory
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


    public void printStats(String name)
    {
        String[] stats = new String[]
                {
                        "Schedudled Thread Pools:",
                        " + _scheduledExecutor:",
                        " |- ActiveThreads:   " + schedudledExecutor.getActiveCount(),
                        " |- getCorePoolSize: " + schedudledExecutor.getCorePoolSize(),
                        " |- PoolSize:        " + schedudledExecutor.getPoolSize(),
                        " |- MaximumPoolSize: " + schedudledExecutor.getMaximumPoolSize(),
                        " |- CompletedTasks:  " + schedudledExecutor.getCompletedTaskCount(),
                        " |- ScheduledTasks:  " + (schedudledExecutor.getTaskCount() - schedudledExecutor.getCompletedTaskCount()),
                        " |- Queue:           " + schedudledExecutor.getQueue().size(),
                        "Schedudled LOW Pools:",
                        " + _scheduledExecutor:",
                        " |- ActiveThreads:   " + executorLow.getActiveCount(),
                        " |- getCorePoolSize: " + executorLow.getCorePoolSize(),
                        " |- PoolSize:        " + executorLow.getPoolSize(),
                        " |- MaximumPoolSize: " + executorLow.getMaximumPoolSize(),
                        " |- CompletedTasks:  " + executorLow.getCompletedTaskCount(),
                        " |- ScheduledTasks:  " + (executorLow.getTaskCount() - executorLow.getCompletedTaskCount()),
                        " |- Queue:           " + executorLow.getQueue().size(),
                        " | -------",
                        " + _executor:",
                        " |- ActiveThreads:   " + executor.getActiveCount(),
                        " |- getCorePoolSize: " + executor.getCorePoolSize(),
                        " |- PoolSize:        " + executor.getPoolSize(),
                        " |- MaximumPoolSize: " + executor.getMaximumPoolSize(),
                        " |- CompletedTasks:  " + executor.getCompletedTaskCount(),
                        " |- ScheduledTasks:  " + (executor.getTaskCount() - executor.getCompletedTaskCount()),
                        " |- Queue:           " + executor.getQueue().size(),
                        " | -------"};

        Log.warning("### Thread Pool Stats: "+ name+" ###");
        for (String s : stats)
            Log.warning(s);
    }


}
