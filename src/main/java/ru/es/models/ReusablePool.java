package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.lang.ESValue;
import ru.es.models.reusablePools.CursorRandomPool;
import ru.es.models.reusablePools.SinglePool;

public abstract class ReusablePool<T>
{
    private final CursorRandomPool<T> pool;

    public ReusablePool(String poolName, int maxLimit, int announceDelaySec)
    {
        ESValue<T> getSet = new ESValue<T>() {
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public T get()
            {
                return createNew();
            }

            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void set(T val)
            {
                clean(val);
            }
        };
        pool = new CursorRandomPool<T>(poolName, maxLimit, announceDelaySec, 10, 10, getSet);
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    protected abstract T createNew();

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public abstract void clean(T t);


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public T getClean()
    {
        return pool.getClean();
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFree(T free)
    {
        pool.addFree(free);
    }


    public void clear()
    {
        pool.clear();
    }

}
