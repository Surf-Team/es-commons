package ru.es.reflection.simple;

import ru.es.lang.StringTable;
import ru.es.log.Log;
import ru.es.reflection.ReflectionUtils;

import org.reflections.Reflections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReflectionManager<T extends ReflectionObject<String>> implements StringTable<T>
{
    private final String reflectionName;
    private final String packageName;
    private final Class<T> tClass;
    private final Map<String, T> objectMap = new HashMap<>();

    // example:
    //String packageName = "ru.es.PolyformStudio.handlers.subWindows";
    // Class<SubWindow>
    public ReflectionManager(String reflectionName, String packageName, Class<T> tClass)
    {
        this.reflectionName = reflectionName;
        this.packageName = packageName;
        this.tClass = tClass;
    }

    public void load(Class<?>[] constructorClasses, Object[] constructorObjects) throws Exception
    {
        // проблемы с AOT graal vm

        Log.warning("ReflectionManager: loading "+reflectionName);
        Reflections reflections = new Reflections(packageName);

        Set<Class<? extends T>> classes = reflections.getSubTypesOf(tClass);

        //List<Class<T>> classes = ReflectionUtils.findClassesInPackage(packageName, getClass().getClassLoader(), tClass);
        // for (Class<T> c : classes)
        for (Class<? extends T> c : classes)
        {
            Log.warning("Scanning class: "+c.getName());
            T newInstance = c.getConstructor(constructorClasses).newInstance(constructorObjects);
            objectMap.put(newInstance.getHandlerName(), newInstance);
            //Log.warning("Loaded handler: "+newInstance.getHandlerName());
        }
    }

    public void add(T instance)
    {
        objectMap.put(instance.getHandlerName(), instance);
    }

    @Override
    public final T getObject(String name)
    {
        return objectMap.get(name);
    }

    public Map<String, T> getObjectMap()
    {
        return objectMap;
    }
}
