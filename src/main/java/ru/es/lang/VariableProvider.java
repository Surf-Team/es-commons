package ru.es.lang;

import org.jdom2.Element;

import java.util.HashMap;
import java.util.Map;

public class VariableProvider
{
	public Map<String, String> variables = new HashMap<>();

	public int getVariableInt(String stringValue)
	{
		String val = variables.get(stringValue);

		if (val == null)
			return Integer.parseInt(stringValue);
		else
			return Integer.parseInt(val);
	}

	public double getVariableDouble(String stringValue)
	{
		String val = variables.get(stringValue);

		if (val == null)
			return Double.parseDouble(stringValue);
		else
			return Double.parseDouble(val);
	}

	public String getVariableString(String defaultValue)
	{
		String val = variables.get(defaultValue);

		if (val == null)
			return defaultValue;
		else return val;
	}

	public void parse(Element variablesXml)
	{
		for (Element e : variablesXml.getChildren("var"))
		{
			variables.put(e.getAttributeValue("name"), e.getAttributeValue("value"));
		}
	}
}
