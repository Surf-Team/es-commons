package ru.es.util;

import ru.es.log.Log;
import ru.es.math.ESMath;

public class CpuMonitor
{
	private long lastCheck = System.currentTimeMillis();
	private long lastCpuMillis = 0;

	public void CpuMonitor()
	{
		getCpuLoad(false);
	}

	// from init or from last check
	public double getCpuLoad(boolean divideByProcessors)
	{
		long totalCpuMillis = ProcessHandle.current().info().totalCpuDuration().get().toMillis();
		//long totalCpuMillis = 0;

		long deltaCpu = totalCpuMillis - lastCpuMillis;
		long deltaTime = System.currentTimeMillis() - lastCheck;

		double load = (double) deltaCpu / deltaTime;
		//Log.warning("Load average: "+load);

		lastCpuMillis = totalCpuMillis;
		lastCheck = System.currentTimeMillis();

		if (divideByProcessors)
			load /= Runtime.getRuntime().availableProcessors();

		load = ESMath.constrain(load, 0, 1);

		return load;
	}
}
