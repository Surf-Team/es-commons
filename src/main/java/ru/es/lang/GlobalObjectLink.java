package ru.es.lang;


import ru.es.services.ObjectManager;

public class GlobalObjectLink<T> // T is not class from scripts
{
	public String uid;
	private final ObjectManager objectManager;

	public GlobalObjectLink(String uid, ObjectManager objectManager)
	{
		this.uid = uid;
		this.objectManager = objectManager;
	}

	public void set(T t)
	{
		objectManager.set(uid, t);
	}

	public T get()
	{
		return objectManager.get(uid);
	}

}
