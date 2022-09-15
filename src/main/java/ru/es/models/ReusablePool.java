package ru.es.models;


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
            
            public T get()
            {
                return createNew();
            }

            @Override
            
            public void set(T val)
            {
                clean(val);
            }
        };
        pool = new CursorRandomPool<T>(poolName, maxLimit, announceDelaySec, 10, 10, getSet);
    }


    
    protected abstract T createNew();

    
    public abstract void clean(T t);


    
    public T getClean()
    {
        return pool.getClean();
    }

    
    public void addFree(T free)
    {
        pool.addFree(free);
    }


    public void clear()
    {
        pool.clear();
    }

}
