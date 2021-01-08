package ru.es.reflection;

import org.apache.commons.lang3.ClassUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by saniller on 17.08.2017.
 */
public abstract class HandleManager<T> implements IHandleManager<T>
{
    public Map<T, Handler<T>> database = new ConcurrentHashMap<>();
    public Set<Handler<T>> cleanable = new HashSet<>();
    public Map<T, Handler<T>> tempDatabase = new ConcurrentHashMap<>();

    public void createNewTemporaryes()
    {
        tempDatabase = new ConcurrentHashMap<>();

        tempDatabase.putAll(database);

        if (cleanCleanableOnReload())
        {
            for (Map.Entry<T, Handler<T>> e : tempDatabase.entrySet())
            {
                if (cleanable.contains(e.getValue()))
                    tempDatabase.remove(e.getKey());
            }
            cleanable.clear();
        }
    }

    // example: return IAdminCommandHandler.class;
    public abstract Class<? extends Handler<T>> getAssignable();

    public abstract boolean cleanCleanableOnReload();

    public void checkRegisterTemporary(Class<?> c)
    {
        Class<?> assignable = getAssignable();
        if(ClassUtils.isAssignable(c, assignable))
        {
            try
            {
                Object o = c.newInstance();
                Handler handler = ((Handler) o);

                registerTemporary(handler, cleanCleanableOnReload());
            }
            catch (InstantiationException e)
            {
                // пропускаем мимо абстрактные классы
            }
            catch(Exception e)
            {
                System.out.println("HandleManager: Failed load " + getClass().getName() + " dynamic handler " + c.getName() + ".");
                e.printStackTrace();
            }
        }
    }

    public void register(Handler<T> h, boolean cleanable)
    {
        for (T s : h.getCommands())
        {
            database.put(s, h);
            tempDatabase.put(s, h);
        }
        if (cleanable)
            this.cleanable.add(h);
    }

    private void registerTemporary(Handler<T> h, boolean cleanable)
    {
        for (T s : h.getCommands())
        {
            tempDatabase.put(s, h);
        }
        if (cleanable)
            this.cleanable.add(h);
    }

    public void acceptTemporaryes()
    {
        database = tempDatabase;
    }

    public  Map<T, Handler<T>> getDatabase()
    {
        return database;
    }

    @Override
    public int getSize()
    {
        return database.size();
    }
}
