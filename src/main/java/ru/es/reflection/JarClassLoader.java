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
    private static int loadersCount = 0;

    public int loaderIndex = loadersCount++;
    private JarResources jarResources;
    private File jarFile;

    private List<Class<?>> dependClasses;

    public JarClassLoader(File jarFile)
    {
        jarResources = new JarResources(jarFile);
        this.jarFile = jarFile;
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
        String url = "jar:file:" + jarFile.getAbsolutePath() + "!/" + name;
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

    public String toString()
    {
        return "JarClassLoader_"+ loaderIndex;
    }
}
