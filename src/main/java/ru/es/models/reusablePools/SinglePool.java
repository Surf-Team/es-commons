package ru.es.models.reusablePools;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.lang.ESValue;
import ru.es.log.Log;
import ru.es.math.Rnd;
import ru.es.models.ESConcurrentPile;
import ru.es.thread.RunnableImpl;
import ru.es.thread.SingletonThreadPool;
import ru.es.util.Environment;

public class SinglePool<T>
{
	String poolName;

	public boolean doLog = true;
	public boolean debug = false;

	int announceDelaySec;
	private final int maxLimit;

	public boolean holderOn = true;

	private final ESConcurrentPile<T> pile;

	private final ESValue<T> createClean;

	// 10000
	public SinglePool(String poolName, int maxLimit, int announceDelaySec, int arrayInitSize, ESValue<T> createClean)
	{
		this.poolName = poolName;
		this.announceDelaySec = announceDelaySec;
		this.maxLimit = maxLimit;
		this.createClean = createClean;

		pile = new ESConcurrentPile<>(arrayInitSize);

		if (!Environment.allowDebug)
			announceDelaySec = 5*60;

		int FannounceDelaySec = announceDelaySec;

		SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl() {
			@Override
			public void runImpl() throws Exception
			{
				String arrayDat = "";
				Log.warning(poolName + ": Sec " + FannounceDelaySec + ", get " + getStat + ", setFree: " + addStat + ", created: " + createdStat+", arraySize: "+pile.arraySizeStat());
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

		T ret = getPileForGet().getOrNull();

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
		getPileForAdd().offer(free);
	}

	private ESConcurrentPile<T> getPileForAdd()
	{
		return pile;
	}

	private ESConcurrentPile<T> getPileForGet()
	{
		return pile;
	}

	public void clear()
	{
		pile.clear();
	}
}
