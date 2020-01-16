package ru.es.thread;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.models.ESArrayList;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;
import javolution.util.FastTable;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 24.08.14
 * Time: 21:36
 * To change this template use File | Settings | File Templates.
 */

public class ESThreadPoolManager
{
    public static boolean createFXThread = true;
    private static ESThreadPoolManager instance;

    public static ESThreadPoolManager getInstance()
    {
        if(instance == null)
            instance = new ESThreadPoolManager();

        return instance;
    }

    public static void deleteInstance()
    {
        if (instance == null)
            return;

        getInstance().stopAll = true;
        getInstance().highSchedudledExecutor.getQueue().clear();
        getInstance().highSchedudledExecutor.shutdownNow();

        getInstance().recordingExecutor.getQueue().clear();
        getInstance().recordingExecutor.shutdownNow();

        getInstance().schedudledExecutor.getQueue().clear();
        getInstance().schedudledExecutor.shutdownNow();

        getInstance().executorHigh.getQueue().clear();
        getInstance().executorHigh.shutdownNow();

        getInstance().executorHigh2.getQueue().clear();
        getInstance().executorHigh2.shutdownNow();

        getInstance().executor.getQueue().clear();
        getInstance().executor.shutdownNow();

        getInstance().schedudledExecutorLow.getQueue().clear();
        getInstance().schedudledExecutorLow.shutdownNow();

        getInstance().lowOneThreadExecutor.getQueue().clear();
        getInstance().lowOneThreadExecutor.shutdownNow();

        getInstance().lowOneThreadExecutor2.getQueue().clear();
        getInstance().lowOneThreadExecutor2.shutdownNow();

        //getInstance().guiTimer.stop();
        getInstance().guiTimer.cancel(false);

        for (Timeline timeline : getInstance().fxTimers)
        {
            timeline.stop();
        }

        getInstance().objectLimitedTasks30.clear();
        getInstance().objectLimitedTasks200.clear();
        getInstance().objectGUILimitedTasks200.clear();
        getInstance().objectLimitedTasks30.clear();
        getInstance().guiTasksFX.clear();
        getInstance().runLaterTasks.clear();

        instance = null;
    }


    public ScheduledThreadPoolExecutor recordingExecutor = new ScheduledThreadPoolExecutor(1, new PriorityThreadFactory("recordingThread", Thread.MIN_PRIORITY, false));
    public ScheduledThreadPoolExecutor schedudledExecutor;
    public ScheduledThreadPoolExecutor highSchedudledExecutor;
    private ThreadPoolExecutor executorHigh;
    public ThreadPoolExecutor executorHigh2;
    private ThreadPoolExecutor executor;
    private ThreadPoolExecutor lowOneThreadExecutor;
    private ThreadPoolExecutor lowOneThreadExecutor2;
    public ScheduledThreadPoolExecutor schedudledExecutorLow;

    //Timeline guiTimer;
    ScheduledFuture guiTimer;
    List<Timeline> fxTimers = new FastTable<>();
    boolean stopAll = false;


    public ConcurrentHashMap<Object, RunnableImpl> objectLimitedTasks3000 = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Object, RunnableImpl> objectLimitedTasks200 = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Object, RunnableImpl> objectGUILimitedTasks200 = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Object, RunnableImpl> objectLimitedTasks30 = new ConcurrentHashMap<>();
    public ConcurrentHashMap<Object, RunnableImpl> guiTasksFX = new ConcurrentHashMap<>();
    public ConcurrentLinkedQueue<Runnable> runLaterTasks = new ConcurrentLinkedQueue<>();


    private ESThreadPoolManager()
    {
        schedudledExecutor = new ScheduledThreadPoolExecutor(2, new PriorityThreadFactory("schedudledExecutor", Thread.NORM_PRIORITY, false));
        schedudledExecutorLow = new ScheduledThreadPoolExecutor(1, new PriorityThreadFactory("schedudledExecutorLow", Thread.MIN_PRIORITY, true));

        highSchedudledExecutor = new ScheduledThreadPoolExecutor(1, new PriorityThreadFactory("highSchedudledExecutor", Thread.MAX_PRIORITY, true));

        int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("ESThreadPoolManager: cores size: "+cores);


        executor = new ThreadPoolExecutor(2, 2, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("executor", Thread.NORM_PRIORITY, false));
        lowOneThreadExecutor = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("logExecutor", Thread.MIN_PRIORITY, false));
        lowOneThreadExecutor2 = new ThreadPoolExecutor(1, 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("logExecutor", Thread.MIN_PRIORITY, false));
        executorHigh = new ThreadPoolExecutor(cores, cores, 50L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("executorHigh",
                Thread.MAX_PRIORITY-1, false));

        executorHigh2 = new ThreadPoolExecutor(cores, cores, 50L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("executorHigh2",
                Thread.MAX_PRIORITY-1, false));


        scheduleGeneralAtFixedRate(new RunnableImpl()
        {
            ESArrayList<Object> toRemove3 = new ESArrayList<>();
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void runImpl()
            {
                toRemove3.clear();
                for (Map.Entry<Object, RunnableImpl> entry : objectLimitedTasks200.entrySet())
                {
                    toRemove3.add(entry.getKey());
                    ESThreadPoolManager.getInstance().executeTask(entry.getValue());
                }

                for (Object o : toRemove3)
                {
                    objectLimitedTasks200.remove(o);
                }
            }
        }, 200, 200);

        runGuiTaskTimer(new RunnableImpl() {
            ESArrayList<Object> toRemove4 = new ESArrayList<>();
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void runImpl() throws Exception
            {
                toRemove4.clear();
                for (Map.Entry<Object, RunnableImpl> entry : objectGUILimitedTasks200.entrySet())
                {
                    toRemove4.add(entry.getKey());
                    entry.getValue().run();
                }

                for (Object o : toRemove4)
                {
                    objectGUILimitedTasks200.remove(o);
                }
            }
        }, 200, true);

        scheduleGeneralAtFixedRate(new RunnableImpl()
        {
            ESArrayList<Object> toRemove2 = new ESArrayList<>();
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void runImpl()
            {
                toRemove2.clear();
                for (Map.Entry<Object, RunnableImpl> entry : objectLimitedTasks3000.entrySet())
                {
                    toRemove2.add(entry.getKey());
                    ESThreadPoolManager.getInstance().executeTask(entry.getValue());
                }

                for (Object o : toRemove2)
                {
                    objectLimitedTasks3000.remove(o);
                }
            }
        }, 3000, 3000);


        scheduleGeneralAtFixedRate(new RunnableImpl()
        {
            ESArrayList<Object> toRemove4 = new ESArrayList<>();
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void runImpl()
            {
                if (!objectLimitedTasks30.isEmpty())
                {
                    toRemove4.clear();

                    for (Map.Entry<Object, RunnableImpl> entry : objectLimitedTasks30.entrySet())
                    {
                        toRemove4.add(entry.getKey());
                        entry.getValue().run();
                    }

                    for (int i = 0; i < toRemove4.size(); i++)
                    {
                        objectLimitedTasks30.remove(toRemove4.get(i));
                    }
                }
            }
        }, 30, 30);

        if (createFXThread)
        {
            Map<Object, RunnableImpl> runned = new HashMap<>();

            guiTimer = scheduleGeneralAtFixedRate(new RunnableImpl()
            {
                @Override
                @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
                public void runImpl() throws Exception
                {
                    if (guiTasksFX.isEmpty() && runLaterTasks.isEmpty())
                        return;


                    Platform.runLater(new RunnableImpl()
                    {
                        @Override
                        @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
                        public void runImpl() throws Exception
                        {
                            if (stopAll)
                                return;

                            runned.clear();
                            runned.putAll(guiTasksFX);

                            for (Map.Entry<Object, RunnableImpl> o : runned.entrySet())
                            {
                                if (stopAll)
                                    return;

                                guiTasksFX.remove(o.getKey());
                                o.getValue().run();
                            }

                            while (!runLaterTasks.isEmpty())
                            {
                                if (stopAll)
                                    return;

                                runLaterTasks.poll().run();
                            }
                        }
                    });
                }
            }, 45, 45);

            /**guiTimer = new Timeline(new KeyFrame(
                    Duration.millis(25),
                    ae -> {

                    }));
            guiTimer.setCycleCount(-1);
            guiTimer.play();**/
        }
    }

    public void recordingExecute(RunnableImpl r, int millis)
    {
        recordingExecutor.schedule(r, millis, TimeUnit.MILLISECONDS);
    }

    public void executeTask(RunnableImpl r)
    {
        executor.execute(r);
    }

    public void execute(Runnable r)
    {
        executor.execute(r);
    }

    public void executeLowOneThread(RunnableImpl r)
    {
        lowOneThreadExecutor.execute(r);
    }

    public void executeLowOneThread2(RunnableImpl r)
    {
        lowOneThreadExecutor2.execute(r);
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

    public ScheduledFuture<?> scheduleUnsafe(Runnable r, long delay)
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
    @Deprecated
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

    @Deprecated
    public ScheduledFuture<?> scheduleGeneralAtFixedRateLow(RunnableImpl r, long initial, long delay)
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

            return schedudledExecutorLow.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
        }
        catch(RejectedExecutionException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public void addObjectLimitedTask3000(Object component, RunnableImpl r)
    {
        if (objectLimitedTasks3000 != null)
            objectLimitedTasks3000.put(component, r);
    }

    public void addObjectLimitedTask200(Object component, RunnableImpl r)
    {
        if (objectLimitedTasks200 != null)
            objectLimitedTasks200.put(component, r);
    }

    public void addGUILimited200(Object component, RunnableImpl r)
    {
        if (objectGUILimitedTasks200 != null)
            objectGUILimitedTasks200.put(component, r);
    }

    public void addObjectLimitedTask30(Object component, RunnableImpl r)
    {
        objectLimitedTasks30.put(component, r);
    }

    public void addGUITask(Object key, RunnableImpl r)
    {
        guiTasksFX.put(key, r);
    }

    public void removeGuiTask(Object key)
    {
        guiTasksFX.remove(key);
    }

    public synchronized void runLater(Runnable r)
    {
        runLaterTasks.offer(r);
    }
    
    public Timeline runGuiTaskTimer(RunnableImpl r, int delay, boolean infinite)
    {
        Timeline timeline = new Timeline (new KeyFrame(
                Duration.millis(delay),
                ae -> {
                    r.run();
                } ) );
        if (infinite)
            timeline.setCycleCount(-1);
        timeline.play();

        fxTimers.add(timeline);
        return timeline;
    }

    public void stopGuiTaskTimer(Timeline t)
    {
        t.stop();
        t.getKeyFrames().clear();
        fxTimers.remove(t);
    }

    public void executeTaskHigh(RunnableImpl r)
    {
        executorHigh.execute(r);
    }
}
