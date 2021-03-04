package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;
import ru.es.thread.RunnableImpl;
import ru.es.thread.SingletonThreadPool;
import ru.es.util.Environment;

public abstract class ReusablePool<T>
{
    String poolName;

    public boolean doLog = true;

    private ThreadLocal<Boolean> needService = ThreadLocal.withInitial(()->false);

    int announceDelaySec;
    private final int maxLimit;

    public boolean holderOn = true;

    private ThreadLocal<ESThreadLocalHeap<T>> container = ThreadLocal.withInitial(() ->
            new ESThreadLocalHeap<T>(100, this::createNew));

    // 10000
    public ReusablePool(String poolName, int maxLimit, int announceDelaySec)
    {
        this.poolName = poolName;
        this.announceDelaySec = announceDelaySec;
        this.maxLimit = maxLimit;

        if (!Environment.allowDebug)
            announceDelaySec = 5*60;

        SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception
            {
                needService = ThreadLocal.withInitial(()->true);
            }
        }, announceDelaySec*1000, announceDelaySec*1000);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    protected abstract T createNew();

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public abstract void clean(T t);

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public T getClean()
    {
        if (!holderOn)
            return createNew();

        T ret = container.get().get();

        if (ret == null)
            throw new RuntimeException("Reusable rrror 10001");

        if (needService.get())
        {
            needService.set(false);
            service();
        }

        return ret;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFree(T free)
    {
        if (!holderOn)
            return;

        if (free == null)
            return;

        clean(free);
        this.container.get().add(free);
    }

    public void clear()
    {
        container = ThreadLocal.withInitial(() ->
                new ESThreadLocalHeap<>(100, this::createNew));
    }

    private void service()
    {
        if (doLog)
        {
            if (container.get().createdStat > 5)
                Log.warning(poolName + " ("+Thread.currentThread().getName()+"): Seconds " + announceDelaySec + ", get " + container.get().getStat + ", setFree: " + container.get().addStat + ", created: " + container.get().createdStat);

            if (container.get().getArraySize() > maxLimit)
            {
                Log.warning(poolName + " ("+Thread.currentThread().getName()+"): Array size too big!!! "+container.get().getArraySize());
                //Thread.dumpStack();
            }
            container.get().flushStat();
        }
    }
}
