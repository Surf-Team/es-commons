package ru.es.util;

import ru.es.log.Log;
import ru.es.thread.RunnableImpl;
import javafx.application.Platform;
import javolution.util.FastTable;

import java.util.List;

/**
 * Created by saniller on 20.10.2016.
 */
public class InstanceUtils
{
    static List<Class> loadedClasses = new FastTable<>();

    public static synchronized void checkDuplicateInstance(Class c)
    {
        Log.warning("Instance Loading: " + c.getName());
        if (loadedClasses.contains(c))
        {
            try
            {
                throw new Exception("Duplicated instance for class "+c.getName());
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Platform.runLater(new RunnableImpl()
                {
                    @Override
                    public void runImpl() throws Exception
                    {
                        ESFXUtils.alertError(null, "Внутренняя ошибка", "Дубликат instance: " + c.getName() + ", " + e.getStackTrace().toString());
                    }
                });
            }
        }
        else
            loadedClasses.add(c);
    }

    public static void clear()
    {
        loadedClasses.clear();
    }
}
