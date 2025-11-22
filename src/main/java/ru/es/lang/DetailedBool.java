package ru.es.lang;

public class DetailedBool
{
	public boolean isTrue;
	public String message;

	public DetailedBool(boolean isTrue, String message)
	{
		this.isTrue = isTrue;
		this.message = message;
	}
	public DetailedBool(boolean isTrue)
	{
		this.isTrue = isTrue;
	}

	public DetailedBool()
	{
	}

	public static DetailedBool TRUE()
	{
		return new DetailedBool(true);
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public static DetailedBool FALSE(String message)
	{
		return new DetailedBool(false, message);
	}
}
