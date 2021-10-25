package ru.es.lang;

public enum Boolean3
{
	True,
	False,
	Pass;

	public static Boolean3 parse(String value)
	{
		if (value.equals("true"))
			value = "True";
		else if (value.equals("false"))
			value = "False";
		else if (value.equals("pass"))
			value = "Pass";

		return Boolean3.valueOf(value);
	}
}
