package ru.es.lang;

public class Invoker<T>
{
	public final String shortName;
	public final String desc;
	public final ESSetterVarargs<T> invoke;
	public final Class<T> tClass;

	public Invoker(Class<T> tClass, String shortName, String desc, ESSetterVarargs<T> invoke)
	{
		this.tClass = tClass;
		this.shortName = shortName;
		this.desc = desc;
		this.invoke = invoke;
	}
}
