package ru.es.xml;

import org.jdom2.Element;
import ru.es.log.Log;
import ru.es.util.FileUtils;

import java.net.URL;
import java.util.*;


/**
 *
 * Класс для загрузки конфигов из xml с поддержкой условий
 * Условии добавляются через initAddCondition
 *
 * example:
 *
 * <root>
 * 	<config name="allowNewbieShop" value="false">
 * 	<case>
 * 		<condition type="day" value="14">         <------ заранее вызываем initAddCase(*) для добавления типов условий
 * 		<config name="allowNewbieShop" value="true">
 * 	</case>    <------ последующие case перезаписыват предыдущие, если в них будут соблюдены условия
 * </root>
 *
 */
public class XmlConfig
{
	public final URL xmlFile;
	public boolean logInfo = true;

	private Map<String, String> stringValueMap;
	private Map<String, Boolean> booleans;
	private Map<String, Integer> ints;
	private Map<String, Long> longs;
	private Map<String, Double> doubles;
	private Map<String, Float> floats;
	private Map<String, List<Integer>> listOfInts;

	private XmlParseConditions xmlParseConditions;

	public XmlConfig(URL xmlFile, XmlParseConditions xmlParseConditions)
	{
		this.xmlFile = xmlFile;
		this.xmlParseConditions = xmlParseConditions;
	}



	public void reloadConfig() throws Exception
	{
		Element root = FileUtils.getXmlDocument(xmlFile);
		Map<String, String> stringValueMap = new HashMap<>();

		addConfigsToMap(stringValueMap, root.getChildren("config"));

		for (Element caseElement : root.getChildren("case"))
		{
			boolean requireOk = xmlParseConditions.checkCondition(caseElement);
			if (requireOk)
				addConfigsToMap(stringValueMap, caseElement.getChildren("config"));
		}

		if (logInfo)
		{
			Log.warning("XmlConfig Current Values:");
			for (Map.Entry<String, String> e : stringValueMap.entrySet())
			{
				Log.warning(e.getKey()+"="+e.getValue());
			}
		}

		construct(stringValueMap);
	}



	private void addConfigsToMap(Map<String, String> stringValueMap, List<Element> config)
	{
		for (Element e : config)
		{
			String name = e.getAttributeValue("name");
			String value = e.getAttributeValue("value");
			stringValueMap.put(name, value);
		}
	}

	private void construct(Map<String, String> stringValueMap)
	{
		Map<String, Boolean> booleans = new HashMap<>();
		Map<String, Integer> ints = new HashMap<>();
		Map<String, Long> longs = new HashMap<>();
		Map<String, Double> doubles = new HashMap<>();
		Map<String, Float> floats = new HashMap<>();
		Map<String, List<Integer>> listOfInts = new HashMap<>();

		for (Map.Entry<String, String> e : stringValueMap.entrySet())
		{
			String val = e.getValue();
			try
			{
				ints.put(e.getKey(), Integer.parseInt(val));
			}
			catch (Exception ex) {}

			try
			{
				longs.put(e.getKey(), Long.parseLong(val));
			}
			catch (Exception ex) {}

			try
			{
				doubles.put(e.getKey(), Double.parseDouble(val));
			}
			catch (Exception ex) {}

			try
			{
				floats.put(e.getKey(), Float.parseFloat(val));
			}
			catch (Exception ex) {}

			try
			{
				booleans.put(e.getKey(), Boolean.parseBoolean(val));
			}
			catch (Exception ex) {}

			try
			{
				List<Integer> newList = new ArrayList<>();
				StringTokenizer t = new StringTokenizer(val, ",");
				while (t.hasMoreTokens())
					newList.add(Integer.parseInt(t.nextToken()));

				listOfInts.put(e.getKey(), newList);
			}
			catch (Exception ex) {}
		}

		this.stringValueMap = stringValueMap;
		this.booleans = booleans;
		this.ints = ints;
		this.longs = longs;
		this.doubles = doubles;
		this.floats = floats;
		this.listOfInts = listOfInts;
	}

	public String getValue(String name)
	{
		return stringValueMap.get(name);
	}

	public float getFloat(String name, float defaultVal)
	{
		Float ret = floats.get(name);
		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public Float getFloat(String name)
	{
		return floats.get(name);
	}

	public int getInt(String name, int defaultVal)
	{
		Integer ret = ints.get(name);
		if (ret == null)
			return defaultVal;
		else
			return ret;
	}


	public Integer getInt(String name)
	{
		return ints.get(name);
	}

	public Long getLong(String name)
	{
		return longs.get(name);
	}

	public String getValue(String name, String defaultVal)
	{
		String ret = stringValueMap.get(name);
		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public double getDouble(String name, double defaultVal)
	{
		Double ret = doubles.get(name);

		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public Double getDouble(String name)
	{
		return doubles.get(name);
	}


	public boolean getBoolean(String name, boolean defaultVal)
	{
		Boolean ret = booleans.get(name);

		if (ret == null)
			return defaultVal;
		else
			return ret;
	}

	public Boolean getBoolean(String name)
	{
		return booleans.get(name);
	}

	public List<Integer> getListOfInt(String name)
	{
		return listOfInts.get(name);
	}
}
