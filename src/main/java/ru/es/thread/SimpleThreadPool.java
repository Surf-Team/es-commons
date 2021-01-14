package ru.es.thread;

import ru.es.log.Log;
import ru.es.math.ESMath;

import java.util.concurrent.*;

public class SimpleThreadPool
{
    public ScheduledThreadPoolExecutor schedudledExecutor;
    private ThreadPoolExecutor executor;

    public SimpleThreadPool()
    {
        int cores = Runtime.getRuntime().availableProcessors();
        cores = ESMath.max(4, cores-1);
        schedudledExecutor = new ScheduledThreadPoolExecutor(cores, new PriorityThreadFactory("schedudledExecutor", Thread.NORM_PRIORITY, false));
        executor = new ThreadPoolExecutor(cores, cores, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("executor", Thread.NORM_PRIORITY, false));
        Log.warning("SimpleThreadPool: cores: "+cores);
    }

    public SimpleThreadPool(String poolName, int threads)
    {
        threads = ESMath.max(1, threads);
        schedudledExecutor = new ScheduledThreadPoolExecutor(threads, new PriorityThreadFactory("schedudled"+poolName, Thread.NORM_PRIORITY, false));
        executor = new ThreadPoolExecutor(threads, threads, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory(poolName, Thread.NORM_PRIORITY, false));
        Log.warning(poolName+": cores: "+threads);
    }

    public void stop()
    {
        schedudledExecutor.getQueue().clear();
        schedudledExecutor.shutdownNow();
        executor.getQueue().clear();
        executor.shutdownNow();
    }

    public void executeTask(RunnableImpl r)
    {
        executor.execute(r);
    }

    public void execute(Runnable r)
    {
        executor.execute(r);
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
                delay = 0;

            if(initial < 0)
                initial = 0;

            return schedudledExecutor.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
        }
        catch(RejectedExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public ScheduledFuture<?> runTimer(Runnable r, long millis, boolean repeat)
    {
        try
        {
            if(millis < 0)
                millis = 0;

            if (repeat)
                return schedudledExecutor.scheduleAtFixedRate(r, millis,
                    millis, TimeUnit.MILLISECONDS);
            else
                return schedudledExecutor.schedule(r, millis, TimeUnit.MILLISECONDS);
        }
        catch(RejectedExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }

}
