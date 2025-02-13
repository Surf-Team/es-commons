package ru.es.lang;

public enum Boolean3
{
	True,
	False,
	Pass;

	public boolean equals(boolean bool)
	{
		if (this == True && bool)
			return true;
		else if (this == False && !bool)
			return true;
		else return this == Pass;
	}

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
