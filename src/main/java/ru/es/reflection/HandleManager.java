package ru.es.reflection;

import org.apache.commons.lang3.ClassUtils;
import ru.es.log.Log;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by saniller on 17.08.2017.
 */
public abstract class HandleManager<T> implements IHandleManager<T>
{
    public Map<T, Handler<T>> database = new ConcurrentHashMap<>();
    public Set<Handler<T>> cleanable = new HashSet<>();
    public Set<Handler<T>> notCleanable = new HashSet<>();
    public Map<T, Handler<T>> tempDatabase = new ConcurrentHashMap<>();

    public void createNewTemporaries()
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
        else
            this.notCleanable.add(h);
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

    public void acceptTemporaries()
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

    public void reload(Collection<Class<?>> classes)
    {
        Log.warning(getClass().getName()+": loading classes...");
        createNewTemporaries();
        for(Class<?> c : classes)
        {
            checkRegisterTemporary(c);
        }
        acceptTemporaries();
        Log.warning(getClass().getName()+" Loaded "+database.size()+" classes");
    }

    // не чистый метод. Не использовать для строительства арзхитектуры
    public List<Handler<T>> getAllHandler()
    {
        List<Handler<T>> handlers = new ArrayList<>();
        handlers.addAll(cleanable);
        handlers.addAll(notCleanable);
        return handlers;
    }

}
