package ru.es.prometheus;

import io.prometheus.client.Gauge;
import ru.es.log.Log;
import ru.es.thread.RunnableImpl;
import ru.es.thread.SingletonThreadPool;
import ru.es.util.Environment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PrometheusNetworkLogger
{
	public final Gauge conntrackCount;
	public final Gauge established;
	public final Gauge syn;
	public final Gauge ack;
	public final Gauge fin1;
	public final Gauge fin2;
	public final Gauge closing;


	public PrometheusNetworkLogger()
	{
		conntrackCount = Gauge.build().name("sysctl_conntrack_count").help("Conntrack count.").register();

		established = Gauge.build().name("netstat_established").help("Established connections.").register();
		syn = Gauge.build().name("netstat_syn").help("SYN_RECV connections.").register();
		ack = Gauge.build().name("netstat_ack").help("LAST_ACK connections.").register();
		fin1 = Gauge.build().name("netstat_fin1").help("FIN_WAIT1 connections.").register();
		fin2 = Gauge.build().name("netstat_fin2").help("FIN_WAIT2 connections.").register();
		closing = Gauge.build().name("netstat_closing").help("CLOSING connections.").register();
	}

	public void startAutoCheck()
	{
		SingletonThreadPool.getInstance().scheduleGeneralAtFixedRate(new RunnableImpl() {
			@Override
			public void runImpl() throws Exception
			{
				checkConntrack();
				checkNetstat();
			}
		}, 10000, 10000);
	}

	public void checkConntrack()
	{
		try
		{
			Process process = Runtime.getRuntime().exec("cat /proc/sys/net/netfilter/nf_conntrack_count");
			String result = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			String[] res = result.split(System.lineSeparator());
			for (String s : res)
			{
				int count = Integer.parseInt(s);
				conntrackCount.set(count);
			}
		}
		catch (IOException e)
		{
			if (Environment.isWindows())
				Log.warning("Security Syn checker is not working.");
			else
				e.printStackTrace();
		}
	}

	public void checkNetstat()
	{
		try
		{
			Process process = Runtime.getRuntime().exec("netstat -na");
			String result = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

			int fullCount = 0;

			String[] logStrings =  new String[] { "ESTABLISHED", "SYN_RECV","LAST_ACK","FIN_WAIT1","FIN_WAIT2","CLOSING" };
			Map<String, AtomicInteger> map = new HashMap<>();
			for (String s : logStrings)
				map.put(s, new AtomicInteger(0));

			String[] res = result.split(System.lineSeparator());
			for (String s : res)
			{
				fullCount++;

				for (String s2 : logStrings)
				{
					if (s.contains(s2))
						map.get(s2).incrementAndGet();
				}
			}


			for (String s2 : logStrings)
			{
				int count = map.get(s2).get();

				if (s2.equals("ESTABLISHED"))
					established.set(count);
				else if (s2.equals("SYN_RECV"))
					syn.set(count);
				else if (s2.equals("LAST_ACK"))
					ack.set(count);
				else if (s2.equals("FIN_WAIT1"))
					fin1.set(count);
				else if (s2.equals("FIN_WAIT2"))
					fin2.set(count);
				else if (s2.equals("CLOSING"))
					closing.set(count);
			}
		}
		catch (IOException e)
		{
			if (Environment.isWindows())
				Log.warning("Security Syn checker is not working.");
			else
				e.printStackTrace();
		}
	}
}
