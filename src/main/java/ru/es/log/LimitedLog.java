package ru.es.log;

import ru.es.util.TimeUtils;

/**
 * Created by saniller on 18.05.2017.
 */
public class LimitedLog
{
    private long lastLogged = 0;

    private final long logDelay;
    private final String logName;
    private long count = 0;

    public LimitedLog(long logDelay, String logName)
    {
        this.logDelay = logDelay;
        this.logName = logName;
    }

    public void doLog()
    {
        long currentTimeMillis = System.currentTimeMillis();
        count++;
        if (lastLogged + logDelay < currentTimeMillis)
        {
            System.out.println(TimeUtils.getTimeForLog()+"LimitedLog # "+logName+": full count: "+count);
            lastLogged = currentTimeMillis;
            count = 0;
        }
    }
}
