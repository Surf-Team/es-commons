package ru.es.thread;

import ru.es.log.Log;

// цикличный поток, у которого можно менять Runnable и sleep
public class ManagedThread
{
	// default empty
	private Runnable runnable = () -> {};
	// default 1s
	public long sleep = 1000L;
	public boolean running = true;
	public Thread thread;

	public ManagedThread(String name)
	{
		thread = new Thread(this::loop);
		thread.setName(name);
		thread.start();;
	}

	public void set(long delay, Runnable runnable)
	{
		this.sleep = delay;
		this.runnable = runnable;
	}

	private void loop()
	{
		while (true)
		{
			try
			{
				try
				{
					runnable.run();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				Thread.sleep(sleep);
			}
			catch (InterruptedException e)
			{
				Log.warning("ManagedThread: "+thread.getName()+" interrupted.");
				break;
			}
		}
		Log.warning("ManagedThread: "+thread.getName()+" stopped.");
	}
}
