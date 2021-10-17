package ru.es.reflection.simple;

import ru.es.reflection.IHandleManager;
import org.apache.commons.lang3.ClassUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Deprecated
public class ReflectionHandleManager<T> implements IHandleManager<T>
{
    private final Class<? extends ReflectionObject<T>> assignable;

    public Map<T, ReflectionObject<T>> database = new ConcurrentHashMap<>();
    public Set<ReflectionObject<T>> cleanable = new HashSet<>();
    public Map<T, ReflectionObject<T>> tempDatabase = new ConcurrentHashMap<>();

    // settings
    public boolean cleanOnReload = true;

    public ReflectionHandleManager(Class<? extends ReflectionObject<T>> assignable)
    {
        this.assignable = assignable;
    }

    public void createNewTemporaries()
    {
        tempDatabase = new ConcurrentHashMap<>();

        tempDatabase.putAll(database);

        if (cleanCleanableOnReload())
        {
            for (Map.Entry<T, ReflectionObject<T>> e : tempDatabase.entrySet())
            {
                if (cleanable.contains(e.getValue()))
                    tempDatabase.remove(e.getKey());
            }
            cleanable.clear();
        }
    }

    // example: return IAdminCommandHandler.class;
    public Class<? extends ReflectionObject<T>> getAssignable()
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
                ReflectionObject handler = ((ReflectionObject) o);

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

    public void register(ReflectionObject<T> h, boolean cleanable)
    {
        database.put(h.getHandlerName(), h);
        tempDatabase.put(h.getHandlerName(), h);
        if (cleanable)
            this.cleanable.add(h);
    }

    private void registerTemporary(ReflectionObject<T> h, boolean cleanable)
    {
        tempDatabase.put(h.getHandlerName(), h);
        if (cleanable)
            this.cleanable.add(h);
    }

    public void acceptTemporaries()
    {
        database = tempDatabase;
    }

    @Override
    public int getSize()
    {
        return database.size();
    }
}
