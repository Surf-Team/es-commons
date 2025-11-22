package ru.es.prometheus;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import ru.es.lang.GlobalObjectLink;
import ru.es.log.Log;
import ru.es.services.ObjectManager;

public class PrometheusManager
{
	private final ObjectManager objectManager;

	private static final String METRIC_REGEX = "[a-zA-Z_:][a-zA-Z0-9_:]*";

	public PrometheusManager(ObjectManager objectManager)
	{
		this.objectManager = objectManager;
	}

	public void gaugeSet(String metricName, int value, String desc)
	{
		if (!isValidMetricName(metricName))
		{
			Log.warning("Invalid metric: "+metricName);
			return;
		}

		GlobalObjectLink<Gauge> globalObjectLink = new GlobalObjectLink<>("prom_gauge_"+metricName, objectManager);

		Gauge g = globalObjectLink.get();

		if (g == null)
		{
			g = Gauge.build().name(metricName).help(desc).register();
			globalObjectLink.set(g);
		}

		g.set(value);
	}

	public void counterInc(String metricName, String desc)
	{
		counterInc(metricName, desc, 1);
	}
	public void counterInc(String metricName, String desc, int count)
	{
		GlobalObjectLink<Counter> globalObjectLink = new GlobalObjectLink<>("prom_counter_"+metricName, objectManager);

		Counter g = globalObjectLink.get();

		if (g == null)
		{
			g = Counter.build().name(metricName).help(desc).register();
			globalObjectLink.set(g);
		}

		g.inc(count);
	}

	public static boolean isValidMetricName(String name)
	{
		return name != null && name.matches(METRIC_REGEX);
	}

}
