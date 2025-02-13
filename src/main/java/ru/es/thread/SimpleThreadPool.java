package ru.es.thread;

import io.prometheus.client.Gauge;
import ru.es.log.Log;
import ru.es.math.ESMath;

import java.util.concurrent.*;

public class SimpleThreadPool
{
    public ScheduledThreadPoolExecutor scheduledExecutor;
    private ThreadPoolExecutor executor;

    private Gauge gauge_executorQuue;
    private Gauge gauge_scheduledExecutorQueue;


    public SimpleThreadPool(String poolName, int min, int max)
    {
        min = ESMath.max(1, min);
        max = ESMath.max(1, max);
        scheduledExecutor = new ScheduledThreadPoolExecutor(min, new PriorityThreadFactory("schedudled"+poolName, Thread.NORM_PRIORITY, false));
        executor = new ThreadPoolExecutor(min, max, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory(poolName, Thread.NORM_PRIORITY, false));
        Log.warning(poolName+": cores: "+min+" to "+max);

        try
        {
            gauge_executorQuue = Gauge.build().name("executorQueue_" + poolName).help("Executor queue for " + poolName).register();
            gauge_scheduledExecutorQueue = Gauge.build().name("scheduledExecutorQueue_" + poolName).help("Scheduled executor queue for " + poolName).register();
        }
        catch (Exception e)
        {
            Log.warning(e.getMessage());
        }

        startService(poolName);
    }

    public SimpleThreadPool(String poolName, int threads)
    {
        this(poolName, threads, threads);
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


    public ScheduledFuture<?> runLoop(Runnable r, long delay)
    {
        try
        {
            // оборачиваем, иначе ошибки не будут видны
            RunnableImpl wrapper = new RunnableImpl() {
                @Override
                public void runImpl() throws Exception
                {
                    r.run();
                }
            };


            return scheduledExecutor.scheduleWithFixedDelay(wrapper, 0, delay, TimeUnit.MILLISECONDS);
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

    private void startService(String poolName)
    {
        SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                int executorQueue = executor.getQueue().size();
                int scheduledExecutorQueue = scheduledExecutor.getQueue().size();

                if (gauge_executorQuue != null)
                {
                    gauge_executorQuue.set(executorQueue);
                    gauge_scheduledExecutorQueue.set(scheduledExecutorQueue);
                }


                if (executorQueue > 10)
                    Log.warning("Queue > 10 for executor thread pool: "+poolName+", queue: "+executorQueue);
            }
        }, 10000, 10000);
    }

}
