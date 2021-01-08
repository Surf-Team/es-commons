package ru.es.reflection;

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

    public JarClassLoader(String jarName)
    {
        jarResources = new JarResources(jarName);
    }

    @Override
    protected byte[] loadClassBytes(String className)
    {
        className = formatClassName(className);
        return jarResources.getResource(className);
    }

    public String[] getClassNames()
    {
        return jarResources.getResources().toArray(new String[] {});
    }
}
