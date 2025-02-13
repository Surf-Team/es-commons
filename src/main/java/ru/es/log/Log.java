package ru.es.log;

import ru.es.util.TimeUtils;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 09.09.14
 * Time: 10:41
 * To change this template use File | Settings | File Templates.
 */
public class Log
{
    private static ESLogger logger = new ESLogger() {
        @Override
        public void warning(String text)
        {
            System.out.println(text);
        }

        @Override
        public void info(String text)
        {
            System.out.println(text);
        }

        @Override
        public void error(String text)
        {
            System.err.println(text);
        }

        @Override
        public void hardGui(String text)
        {
            System.out.println(text);
        }

        @Override
        public void debug(String text)
        {
            System.out.println(text);
        }

        @Override
        public void event(String text)
        {
            System.out.println(text);
        }
    };

    public static void setLogger(ESLogger _logger, File errorOutputFile, boolean changeErrorStream) throws IOException
    {
        logger = _logger;
        if (!errorOutputFile.getParentFile().exists())
        {
            errorOutputFile.getParentFile().mkdirs();
            errorOutputFile.createNewFile();
        }

        PrintStream fio = new PrintStream(new FileOutputStream(errorOutputFile));

        if (changeErrorStream)
        {
            System.setOut(fio);
            System.setErr(fio);
        }
    }

    public static void setLogger(ESLogger _logger)
    {
        logger = _logger;
    }

    public static void warning(String text)
    {
        logger.warning(TimeUtils.getTimeForLog()+text);
    }

    // логирование тяжёлых GUI перерисовок (например, которые не должны срабатывать чаще чем 2 раза в 1 секунду)
    public static void hardGUI(Class classObj, String text)
    {

    }

    public static void info(String text)
    {
        logger.info(text);
    }

    public static void event(String text)
    {
        logger.event(text);
    }

    public static void error(String text)
    {
        logger.error(text);
    }

    public static void debug(String text)
    {
        logger.debug(text);
    }

    @Deprecated
    public static void fine(String s)
    {
        logger.fine(s);
    }
}
