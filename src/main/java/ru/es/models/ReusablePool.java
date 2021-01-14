package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;
import ru.es.thread.RunnableImpl;
import ru.es.thread.SingletonThreadPool;
import ru.es.util.Environment;

import java.util.concurrent.ArrayBlockingQueue;

public abstract class ReusablePool<T>
{
    int cycleIndex = 0;
    final boolean cleanDelayed;
    String poolName;

    public int lastDirtySize = 50;
    public int lastFreeSize = 50;

    public boolean doLog = true;

    // 10000
    public ReusablePool(String poolName, boolean cleanDelayed, int maxLimit, int announceDelaySec)
    {
        if (!Environment.allowDebug)
            announceDelaySec = 5*60*1000;

        int FannounceDelaySec = announceDelaySec;

        this.cleanDelayed = cleanDelayed;
        this.poolName = poolName;

        reinstallDirty(lastDirtySize);
        reinstallClean(lastFreeSize);

        if (cleanDelayed)
        {
            SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl()
            {
                @Override
                @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
                public void runImpl() throws Exception
                {
                    int cleaned = 0;
                    
                    while (!dirty.isEmpty())
                    {
                        T nextDirty = dirty.poll();
                        if (nextDirty != null)
                        {
                            clean(nextDirty);
                            cleaned++;
                            if (!clean.offer(nextDirty))
                            {
                                synchronized (clean)
                                {
                                    if (!clean.offer(nextDirty))
                                    {
                                        lastFreeSize *= 2;
                                        Log.warning(poolName + " increase clean size to " + lastFreeSize);
                                        reinstallClean(lastFreeSize);
                                        clean.offer(nextDirty);
                                    }
                                }
                            }
                        }
                    }
                    //Log.warning("cleaned: "+cleaned);
                }
            }, 100, 100);
        }

        if (announceDelaySec != -1)
        {
            SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl()
            {
                @Override
                @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
                public void runImpl() throws Exception
                {
                    cycleIndex++;
                    //if (cycleIndex % 6 == 0)
                    if (doLog)
                    {
                        if (created > 500)
                            Log.warning(poolName + ": Try get in " + FannounceDelaySec + " secs: " + tryGet + ", setFree: " + settedFree + ", created: " + created + ", clean: " +
                                clean.size() + ", dirty: " + dirty.size());
                    }


                    if (clean.size() > maxLimit)
                    {
                        Log.warning(poolName + ": Free objects too much! Cleaning to " + maxLimit + "...");
                        int removedSize = 0;
                        while (clean.size() > maxLimit)
                        {
                            clean.poll();
                            removedSize++;
                        }
                        Log.warning(poolName + ": Free objects removed: " + removedSize);
                    }

                    created = 0;
                    tryGet = 0;
                    settedFree = 0;
                }
            }, announceDelaySec * 1000, announceDelaySec * 1000);
        }
    }

    private void reinstallDirty(int size)
    {
        dirty = new ArrayBlockingQueue<>(size, false);
    }

    private void reinstallClean(int size)
    {
        clean = new ArrayBlockingQueue<T>(size, false);
    }

    public ArrayBlockingQueue<T> clean;
    public ArrayBlockingQueue<T> dirty;


    int created = 0;
    int settedFree = 0;
    int tryGet = 0;

    public boolean holderOn = true;

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    protected abstract T createNew();

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public abstract void clean(T t);

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public T getClean()
    {
        if (!holderOn)
            return createNew();

        T ret = clean.poll();

        if (ret == null)
        {
            ret = createNew();
            created++;
        }
        tryGet++;

        return ret;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFree(T free)
    {
        if (!holderOn)
            return;

        if (free == null)
            return;

        if (cleanDelayed)
        {
            if (!dirty.offer(free))
            {
                synchronized (dirty)
                {
                    if (!dirty.offer(free))
                    {
                        lastDirtySize *= 2;
                        Log.warning(poolName + " increase dirty size to " + lastDirtySize);

                        ArrayBlockingQueue<T> oldList = dirty;
                        reinstallDirty(lastDirtySize);
                        dirty.offer(free);
                        dirty.addAll(oldList);
                    }
                }
            }
        }
        else
        {
            clean(free);
            if (!this.clean.offer(free))
            {
                synchronized (clean)
                {
                    if (!this.clean.offer(free))
                    {
                        lastFreeSize *= 2;
                        Log.warning(poolName + " increase clean size to " + lastFreeSize);
                        reinstallClean(lastFreeSize);
                        this.clean.offer(free);
                    }
                }
            }
        }

        settedFree++;
    }
}
