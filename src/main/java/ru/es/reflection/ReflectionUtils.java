package ru.es.reflection;

import ru.es.log.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils
{
    public static<T> List<Class<T>> findClassesInPackage(String packageName, ClassLoader classLoader, Class<T> classType)
            throws ClassNotFoundException, UnsupportedEncodingException
    {
        List<Class<T>> classes = new ArrayList<>();

        URL root = classLoader.getResource(packageName.replace(".", "/"));
        Log.warning("Loading here: "+root);

        // Filter .class files.
        File[] files = new File(URLDecoder.decode(root.getFile(), "UTF-8")).listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        });


        // Find classes
        for (File file : files) {
            String className = file.getName().replaceAll(".class$", "");
            Class<?> cls = Class.forName(packageName + "." + className);
            if (classType.isAssignableFrom(cls)) {
                classes.add((Class<T>) cls);
            }
        }

        return classes;
    }
}
