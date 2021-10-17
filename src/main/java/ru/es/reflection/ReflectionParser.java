package ru.es.reflection;

import ru.es.lang.ESEventHandler;
import ru.es.log.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReflectionParser
{
    String name;

    private final Map<String, Class<?>> loadedClasses = new ConcurrentHashMap<>();
    private final List<Class<?>> staticHandleClasses = new ArrayList<>();
    public final ConcurrentLinkedQueue<IHandleManager> handleManagers = new ConcurrentLinkedQueue<>();

    ESEventHandler<File> onExceptionOnLoadJar = new ESEventHandler<>();
    ESEventHandler<File> onJarNotFound = new ESEventHandler<>();

    public ReflectionParser(String debugName)
    {
        this.name = debugName;
    }

    public void registerHandleManager(IHandleManager h)
    {
        handleManagers.add(h);

        h.createNewTemporaries();
        for (Class<?> clazz : loadedClasses.values())
        {
            h.checkRegisterTemporary(clazz);
        }
        h.acceptTemporaries();

        Log.warning("ESReflections "+name+": registered handle manager "+h.getClass().getName()+". Assigned "+h.getSize()+" classes.");
    }

    public void initStaticClasses(List<Class<?>> classes)
    {
        staticHandleClasses.addAll(classes);
    }

    // File f = new File("./Scripts.jar");
    public void load(File... jarFiles)
    {
        Log.warning("ESReflections "+name+": Loading...");

        loadedClasses.clear();

        List<Class<?>> classesGlobal = new ArrayList<Class<?>>();
        Class<?> c;


        for (File f : jarFiles)
        {
            List<Class<?>> classes = new ArrayList<Class<?>>();
            if (f.exists())
            {
                JarClassLoader jcl = null;
                try
                {
                    jcl = new JarClassLoader(f.getAbsolutePath());
                    for (String name : jcl.getClassNames())
                    {
                        if (!name.contains(".class"))
                            continue;
                        if (name.contains("$"))
                            continue; // пропускаем вложенные классы
                        name = name.replace(".class", "").replace("/", ".");
                        c = jcl.loadClass(name);
                        classes.add(c);
                    }
                }
                catch (Exception e)
                {
                    onExceptionOnLoadJar.event(f);
                    Log.warning("Fail to load "+f.getName()+".jar!");
                    e.printStackTrace();
                    classes.clear();
                }
                finally
                {
                    jcl = null;
                    //if (jcl != null)
                    //    IOUtils.closeQuietly(jcl);
                }
                classesGlobal.addAll(classes);
            }
            else
            {
                Log.warning("File not found!");
                Log.warning("scripts file: " + f.getAbsolutePath());
                onJarNotFound.event(f);
            }
        }


        classesGlobal.addAll(staticHandleClasses); // те, котоыре добавляем вручную

        Log.warning("ESReflections "+name+": Loaded " + classesGlobal.size() + " classes.");

        Class<?> clazz;
        for(int i = 0; i < classesGlobal.size(); i++)
        {
            clazz = classesGlobal.get(i);
            loadedClasses.put(clazz.getName(), clazz);
        }

        for (IHandleManager h : handleManagers)
        {
            Log.warning("ESReflections "+name+": checking handleManager: "+h.getClass().getName());
            h.createNewTemporaries();
            for(int i = 0; i < classesGlobal.size(); i++)
            {
                clazz = classesGlobal.get(i);
                h.checkRegisterTemporary(clazz);
            }
            h.acceptTemporaries();
            Log.warning("ESReflections "+name+": Loaded "+h.getSize()+" classes for "+h.getClass().getName());
        }
    }


    public Map<String, Class<?>> getClasses()
    {
        return loadedClasses;
    }
}
