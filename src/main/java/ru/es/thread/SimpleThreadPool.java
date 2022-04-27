package ru.es.thread;

import ru.es.log.Log;
import ru.es.math.ESMath;

import java.util.concurrent.*;

public class SimpleThreadPool
{
    public ScheduledThreadPoolExecutor scheduledExecutor;
    private ThreadPoolExecutor executor;

    public SimpleThreadPool()
    {
        int cores = Runtime.getRuntime().availableProcessors();
        cores = ESMath.max(4, cores-1);
        scheduledExecutor = new ScheduledThreadPoolExecutor(cores, new PriorityThreadFactory("schedudledExecutor", Thread.NORM_PRIORITY, false));
        executor = new ThreadPoolExecutor(cores, cores, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("executor", Thread.NORM_PRIORITY, false));
        Log.warning("SimpleThreadPool: cores: "+cores);
    }

    public SimpleThreadPool(String poolName, int threads)
    {
        threads = ESMath.max(1, threads);
        scheduledExecutor = new ScheduledThreadPoolExecutor(threads, new PriorityThreadFactory("schedudled"+poolName, Thread.NORM_PRIORITY, false));
        executor = new ThreadPoolExecutor(threads, threads, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory(poolName, Thread.NORM_PRIORITY, false));
        Log.warning(poolName+": cores: "+threads);
    }

    public void stop()
    {
        scheduledExecutor.getQueue().clear();
        scheduledExecutor.shutdownNow();
        executor.getQueue().clear();
        executor.shutdownNow();
    }

    public void executeTask(RunnableImpl r)
    {
        executor.execute(r);
    }

    public void execute(Runnable r)
    {
        // оборачиваем, иначе ошибки не будут видны
        executor.execute(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                r.run();
            }
        });
    }

    public ScheduledFuture<?> runOnce(Runnable r, long delay)
    {
        return runOnce(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                r.run();
            }
        }, delay);
    }

    public ScheduledFuture<?> runOnce(RunnableImpl r, long delay)
    {
        try
        {
            if(delay < 0)
            {
                delay = 0;
            }

            return scheduledExecutor.schedule(r, delay, TimeUnit.MILLISECONDS);
        }
        catch(Exception e)
        {
            //shutdown, ignore
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

            // оборачиваем, иначе ошибки не будут видны
            RunnableImpl wrapper = new RunnableImpl() {
                @Override
                public void runImpl() throws Exception
                {
                    r.run();
                }
            };


            if (repeat)
                return scheduledExecutor.scheduleAtFixedRate(wrapper, millis,
                    millis, TimeUnit.MILLISECONDS);
            else
                return scheduledExecutor.schedule(wrapper, millis, TimeUnit.MILLISECONDS);
        }
        catch(RejectedExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public void printStats(String name)
    {
        String[] stats = new String[]
                {
                        "Schedudled Thread Pools:",
                        " + _scheduledExecutor:",
                        " |- ActiveThreads:   " + scheduledExecutor.getActiveCount(),
                        " |- getCorePoolSize: " + scheduledExecutor.getCorePoolSize(),
                        " |- PoolSize:        " + scheduledExecutor.getPoolSize(),
                        " |- MaximumPoolSize: " + scheduledExecutor.getMaximumPoolSize(),
                        " |- CompletedTasks:  " + scheduledExecutor.getCompletedTaskCount(),
                        " |- ScheduledTasks:  " + (scheduledExecutor.getTaskCount() - scheduledExecutor.getCompletedTaskCount()),
                        " |- Queue:           " + scheduledExecutor.getQueue().size(),
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

    public void shutdown() throws InterruptedException
    {
        try
        {
            scheduledExecutor.shutdown();
            scheduledExecutor.awaitTermination(2, TimeUnit.SECONDS);
        }
        finally
        {
            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS);
        }
    }
}
