package ru.es.xml;

import org.jdom2.Element;
import ru.es.lang.Converter;

import java.util.HashMap;
import java.util.Map;

public class XmlParseConditions
{
	protected final Map<String, Converter<String, Boolean>> conditionCheckers = new HashMap<>();


	// run before reloadConfigs()
	public void initAddCondition(String type, Converter<String, Boolean> check)
	{
		conditionCheckers.put(type, check);
	}

	public boolean checkCondition(Element caseElement) throws Exception
	{
		boolean requireOk = true;
		for (Element req : caseElement.getChildren("condition"))
		{
			String type = req.getAttributeValue("type");
			String valueStr = req.getAttributeValue("value");

			Converter<String, Boolean> caseChecker = conditionCheckers.get(type);

			if (caseChecker == null)
				throw new Exception("Case checker not found: " + type);

			boolean ok = caseChecker.convert(valueStr);
			if (!ok)
				requireOk = false;
		}
		return requireOk;
	}
}
