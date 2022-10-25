package ru.es.reflection;


import ru.es.log.Log;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 30.12.14
 * Time: 2:47
 * To change this template use File | Settings | File Templates.
 */
public class JarClassLoader extends MultiClassLoader
{
    private JarResources jarResources;
    private File jarName;

    private List<Class<?>> dependClasses;

    public JarClassLoader(String jarName)
    {
        jarResources = new JarResources(jarName);
        this.jarName = new File(jarName);
    }

    @Override
    protected byte[] loadClassBytes(String className)
    {
        className = formatClassName(className);
        return jarResources.getResource(className);
    }


    public synchronized Class<?> loadClass(String className, boolean resolveIt) throws ClassNotFoundException
    {
        if (dependClasses != null)
        {
            for (Class c : dependClasses)
            {
                if (c.getName().equals(className))
                    return c;
            }
        }
        return super.loadClass(className, resolveIt);
    }

    public String[] getClassNames()
    {
        return jarResources.getResources().toArray(new String[] {});
    }

    @Override
    protected URL findResource(String name)
    {
        String url = "jar:file:" + jarName.getAbsolutePath() + "!/" + name;
        try
        {
            return new URL(url);
        }
        catch (Exception e)
        {
            Log.warning("resource not found: "+url);
            e.printStackTrace();
            return null;
        }
    }

    public void setDependClasses(List<Class<?>> dependClasses)
    {
        this.dependClasses = dependClasses;
    }
}
