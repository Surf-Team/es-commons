package ru.es.models.reusablePools;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.lang.ESValue;
import ru.es.log.Log;
import ru.es.math.Rnd;
import ru.es.models.ESConcurrentPile;
import ru.es.thread.RunnableImpl;
import ru.es.thread.SingletonThreadPool;
import ru.es.util.Environment;

public class CursorRandomPool<T>
{
	String poolName;

	public boolean doLog = true;
	public boolean debug = false;

	int announceDelaySec;
	private final int maxLimit;
	private final int randomsCount;

	public boolean holderOn = true;

	private final ESConcurrentPile<T> piles[];

	private final ThreadLocal<ThreadCursor> pilePointerGet;
	private final ThreadLocal<ThreadCursor> pilePointerAdd;

	private static class ThreadCursor
	{
		private int cursor = 0;
		private int nextRandom = 100_000;
		private final int randomsCount;

		public ThreadCursor(int cursor, int randomsCount)
		{
			this.cursor = cursor;
			this.randomsCount = randomsCount;
		}

		private int getNext()
		{
			cursor++;

			if (cursor > randomsCount)
				cursor = 0;

			nextRandom--;
			if (nextRandom == 0)
			{
				nextRandom = 100_000;
				cursor = Rnd.get(0, randomsCount);
			}

			return cursor;
		}
	}

	private final ESValue<T> createClean;

	// 10000
	public CursorRandomPool(String poolName, int maxLimit, int announceDelaySec, int randomsCount, int arrayInitSize, ESValue<T> createClean)
	{
		this.poolName = poolName;
		this.announceDelaySec = announceDelaySec;
		this.maxLimit = maxLimit;
		this.createClean = createClean;
		this.piles = new ESConcurrentPile[randomsCount];
		this.randomsCount = randomsCount;

		for (int i = 0; i < randomsCount; i++)
			piles[i] = new ESConcurrentPile<>(arrayInitSize);

		pilePointerGet = ThreadLocal.withInitial(()-> new ThreadCursor(Rnd.get(0, randomsCount-1), randomsCount-1));
		pilePointerAdd = ThreadLocal.withInitial(()-> new ThreadCursor(Rnd.get(0, randomsCount-1), randomsCount-1));


		if (!Environment.allowDebug)
			announceDelaySec = 5*60;

		int FannounceDelaySec = announceDelaySec;

		SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl() {
			@Override
			public void runImpl() throws Exception
			{
				String arrayDat = "";
				for (ESConcurrentPile p : piles)
				{
					arrayDat += p.arraySizeStat()+" ";
				}
				//Log.warning(poolName + ": Sec " + FannounceDelaySec + ", get " + getStat + ", setFree: " + addStat + ", created: " + createdStat+", arraySize: "+arrayDat);
				createdStat = 0;
				addStat = 0;
				getStat = 0;
			}
		}, announceDelaySec*1000, announceDelaySec*1000);
	}

	private int createdStat = 0;
	private int addStat = 0;
	private int getStat = 0;

	@ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
	protected T createNew()
	{
		return createClean.get();
	}

	@ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
	public void clean(T t)
	{
		createClean.set(t);
	}


	@ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
	public T getClean()
	{
		if (!holderOn)
			return createNew();

		T ret = piles[pilePointerGet.get().getNext()].getOrNull();

		getStat++;

		if (ret == null)
		{
			ret = createNew();
			createdStat++;
		}

		if (debug)
		{
			inUse++;
			Log.warning("inUse: "+ inUse);
		}

		return ret;
	}

	public int inUse = 0;

	@ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
	public void addFree(T free)
	{
		if (!holderOn)
			return;

		if (free == null)
			return;

		if (debug)
		{
			inUse--;
			Log.warning("inUse (del): "+ inUse);
		}

		addStat++;
		clean(free);
		piles[pilePointerAdd.get().getNext()].offer(free);
	}



	public void clear()
	{
		for (ESConcurrentPile p : piles)
			p.clear();
	}
}
