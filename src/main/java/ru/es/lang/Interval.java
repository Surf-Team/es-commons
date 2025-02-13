package ru.es.lang;

public class Interval
{
	public int from;
	public int to;

	public Interval(int from, int to)
	{
		this.from = from;
		this.to = to;
	}

	public boolean isInside(int num)
	{
		return from <= num && num <= to;
	}
}
