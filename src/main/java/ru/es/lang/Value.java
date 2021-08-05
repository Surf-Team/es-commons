package ru.es.lang;

public class Value<T> implements ESValue<T>
{
	T value;

	public Value(T value)
	{
		this.value = value;
	}

	@Override
	public T get()
	{
		return value;
	}

	@Override
	public void set(T newValue)
	{
		value = newValue;
	}
}
