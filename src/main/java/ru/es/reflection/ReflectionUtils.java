package ru.es.reflection;

import org.apache.commons.lang3.ClassLoaderUtils;
import org.apache.commons.lang3.ClassUtils;
import ru.es.log.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.tools.*;

public class ReflectionUtils
{
    // with superclasses
    public static List<Field> getAllFields(Class objectClass)
    {
        List<Class> classes = new ArrayList<>();
        classes.add(objectClass);
        Class currentClass = objectClass;
        while (currentClass.getSuperclass() != null)
        {
            currentClass = currentClass.getSuperclass();
            classes.add(currentClass);
        }

        List<Field> ret = new ArrayList<>();
        for (Class c : classes)
        {
            ret.addAll(List.of(c.getDeclaredFields()));
        }
        return ret;
    }


    public static<T> List<Class<T>> findClassesInPackage(String packageName, ClassLoader classLoader, Class<T> classType)
            throws Exception
    {
        /*
// первый вариант = особо не прорабатывал, загружает не классы а какие то "обёртки классов", из которых по сути можно потом выбрать классы
// но требует компилятор, т.е. jdk
        List<Class> commands = new ArrayList<>();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        JavaFileManager.Location location = StandardLocation.CLASS_PATH;
        Set<JavaFileObject.Kind> kinds = new HashSet<>();
        kinds.add(JavaFileObject.Kind.CLASS);
        boolean recurse = false;

        Iterable<JavaFileObject> list = fileManager.list(location, packageName,
                kinds, recurse);

        for (JavaFileObject javaFileObject : list)
        {
            Log.warning("Found java file object: "+javaFileObject.getName());
            commands.add(javaFileObject.getClass());
        }

        List<Class<T>> classes = new ArrayList<>();

        for (Class c : commands)
        {
            Log.warning("Checking class: "+c.getName());
            if (classType.isAssignableFrom(c))
            {
                Log.warning("Imported!");
                classes.add((Class<T>) c);
            }
            else
                Log.warning("Is not assignable");
        }                     */


        // второй вариант работает, но только внутри одного jar файла

        List<Class<T>> classes = new ArrayList<>();
        URL root = classLoader.getResource(packageName.replace(".", "/"));
        Log.warning("Loading here: "+root);

        // Filter .class files.
        File folder = new File(URLDecoder.decode(root.getFile(), "UTF-8"));
        Log.warning("Folder path: "+folder.getAbsolutePath());
        Log.warning("folder exists: "+folder.exists());

        File[] files = folder.listFiles((dir, name) ->
        {
            Log.warning("Checking file: "+name);
            return name.endsWith(".class");
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
