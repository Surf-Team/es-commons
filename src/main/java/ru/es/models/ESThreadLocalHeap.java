package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.lang.ESGetter;
import ru.es.log.Log;


public class ESThreadLocalHeap<K>
{
	static int test = 0;

	public static void main(String[] args)
	{
		ESThreadLocalHeap<String> heap = new ESThreadLocalHeap<>(10, ()->"test:"+test++);

		for (int i = 0; i < 50; i++)
		{
			Log.warning("get: "+heap.get());
		}

		for (int i = 0; i < 50; i++)
		{
			heap.add("abc"+i);
		}


		for (int i = 0; i < 100; i++)
		{
			Log.warning("get2: "+heap.get());
		}
	}

	private K[] array;
	private int currentIndex = -1;
	private final ESGetter<K> createNew;

	public int createdStat = 0;
	public int getStat = 0;
	public int addStat = 0;

	public ESThreadLocalHeap(int initArraySize, ESGetter<K> createNew)
	{
		array = createArray(initArraySize);
		this.createNew = createNew;
	}

	@ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
	protected K[] createArray(int size)
	{
		return (K[]) new Object[size];
	}

	@ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
	public K get()
	{
		getStat++;
		if (currentIndex == -1)
		{
			createdStat++;
			return createNew.get();
		}
		
		return array[currentIndex--];
	}

	@ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
	public void add(K object)
	{
		addStat++;

		currentIndex++;
		if (currentIndex >= array.length)
		{
			K[] newKeyArray = createArray(currentIndex * 3);
			System.arraycopy(array, 0, newKeyArray, 0, array.length);
			array = newKeyArray;
		}
		array[currentIndex] = object;
	}


	public void flushStat()
	{
		createdStat = 0;
		getStat = 0;
		addStat = 0;
	}

	public int getArraySize()
	{
		return array.length;
	}
}
