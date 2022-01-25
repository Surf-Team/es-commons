package ru.es.lang;

public abstract class ConditionChecker
{
	public final String desc;

	protected ConditionChecker(String desc)
	{
		this.desc = desc;
	}

	public abstract boolean ok();
}
