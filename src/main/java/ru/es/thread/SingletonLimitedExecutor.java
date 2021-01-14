package ru.es.thread;


public class SingletonLimitedExecutor extends LimitedExecutor
{
    private static SingletonLimitedExecutor instance;

    public static SingletonLimitedExecutor getInstance()
    {
        if (instance == null)
        {
            TimeEventHandler clockEventHandler = new TimeEventHandler();
            instance = new SingletonLimitedExecutor(clockEventHandler);
            SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl() {
                @Override
                public void runImpl() throws Exception
                {
                    clockEventHandler.run();
                }
            }, 40, 40);

        }

        return instance;
    }


    public SingletonLimitedExecutor(TimeEventHandler timeEventHandler)
    {
        super(timeEventHandler);
    }
}
