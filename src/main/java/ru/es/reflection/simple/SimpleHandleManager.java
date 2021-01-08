package ru.es.reflection.simple;

import ru.es.reflection.IHandleManager;
import org.apache.commons.lang3.ClassUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleHandleManager<T> implements IHandleManager<T>
{
    private final Class<? extends SimpleHandler<T>> assignable;

    public Map<T, SimpleHandler<T>> database = new ConcurrentHashMap<>();
    public Set<SimpleHandler<T>> cleanable = new HashSet<>();
    public Map<T, SimpleHandler<T>> tempDatabase = new ConcurrentHashMap<>();

    // settings
    public boolean cleanOnReload = true;

    public SimpleHandleManager(Class<? extends SimpleHandler<T>> assignable)
    {
        this.assignable = assignable;
    }

    public void createNewTemporaryes()
    {
        tempDatabase = new ConcurrentHashMap<>();

        tempDatabase.putAll(database);

        if (cleanCleanableOnReload())
        {
            for (Map.Entry<T, SimpleHandler<T>> e : tempDatabase.entrySet())
            {
                if (cleanable.contains(e.getValue()))
                    tempDatabase.remove(e.getKey());
            }
            cleanable.clear();
        }
    }

    // example: return IAdminCommandHandler.class;
    public Class<? extends SimpleHandler<T>> getAssignable()
    {
        return assignable;
    }

    public boolean cleanCleanableOnReload()
    {
        return true;
    }


    public void checkRegisterTemporary(Class<?> c)
    {
        Class<?> assignable = getAssignable();
        if(ClassUtils.isAssignable(c, assignable))
        {
            try
            {
                Object o = c.newInstance();
                SimpleHandler handler = ((SimpleHandler) o);

                registerTemporary(handler, cleanCleanableOnReload());
            }
            catch (InstantiationException e)
            {
                // пропускаем мимо абстрактные классы
            }
            catch(Exception e)
            {
                System.out.println("SimpleHandleManager: Failed load " + getClass().getName() + " dynamic handler " + c.getName() + ".");
                e.printStackTrace();
            }
        }
    }

    public void register(SimpleHandler<T> h, boolean cleanable)
    {
        database.put(h.getUID(), h);
        tempDatabase.put(h.getUID(), h);
        if (cleanable)
            this.cleanable.add(h);
    }

    private void registerTemporary(SimpleHandler<T> h, boolean cleanable)
    {
        tempDatabase.put(h.getUID(), h);
        if (cleanable)
            this.cleanable.add(h);
    }

    public void acceptTemporaryes()
    {
        database = tempDatabase;
    }

    @Override
    public int getSize()
    {
        return database.size();
    }
}
