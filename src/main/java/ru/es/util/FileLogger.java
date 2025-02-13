package ru.es.util;

import ru.es.log.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileLogger
{
    private final FileOutputStream fileOutputStream;
    public int recordsLimit = 100000;
    private int currentLogIndex = 0;

    public FileLogger(File file) throws IOException
    {
        file.getParentFile().mkdir();

        if (file.exists())
            file.renameTo(new File(file.getParent(), file.getName()+" "+ TimeUtils.getCurrentTimeStringForFile()));

        fileOutputStream = new FileOutputStream(file);
    }

    public void log(String log)
    {
        currentLogIndex++;
        String text = null;

        if (currentLogIndex > recordsLimit)
            return;
        else if (currentLogIndex == recordsLimit)
            text = TimeUtils.getTimeForLog()+"Log size limit!";
        else
            text = currentLogIndex+" "+TimeUtils.getTimeForLog()+": "+log+System.lineSeparator();

        try
        {
            fileOutputStream.write(text.getBytes());
        }
        catch (IOException e)
        {
            Log.warning("Cant write log: "+e.getMessage()+",  "+log);
        }
    }
}
