package ru.es.models;


import ru.es.log.Log;

import java.util.concurrent.atomic.AtomicBoolean;


public class ESConcurrentPile<K>
{
	public static void main(String[] args)
	{
		ESConcurrentPile<String> ss = new ESConcurrentPile<>(10);
		for (int i = 0 ; i< 20; i++)
		{
			ss.offer("Hi_"+i);
		}
		for (int i = 0 ; i< 30; i++)
		{
			Log.warning(ss.getOrNull());
		}
		for (int i = 0 ; i< 40; i++)
		{
			ss.offer("Hi2_"+i);
		}
		for (int i = 0 ; i< 50; i++)
		{
			Log.warning(ss.getOrNull());
		}
	}

	private K[] array;
	private int writeCursor = 0;
	private final Object sync = new Object();

	public ESConcurrentPile(int initSize)
	{
		array = createArray(initSize);
	}

	
	private K[] createArray(int size)
	{
		return (K[]) new Object[size];
	}

	
	public void offer(K free)
	{
		synchronized (sync)
		{
			if (writeCursor >= array.length)
			{
				K[] newArray = createArray(array.length*3);
				System.arraycopy(array, 0, newArray, 0, writeCursor);

				array = newArray;
				Log.warning("Recreate pile array. new size: "+array.length);
			}
			array[writeCursor] = free;
			writeCursor++;
		}
	}

	
	public K getOrNull()
	{
		synchronized (sync)
		{
			if (writeCursor == 0)
				return null;

			return array[--writeCursor];
		}
	}

	
	public void clear()
	{
		synchronized (sync)
		{
			writeCursor = 0;
		}
	}

	public int arraySizeStat()
	{
		return array.length;
	}
}
