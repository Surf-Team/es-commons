package ru.es.exceptions;

// исключение, о котором должен знать пользователь
public class PublicException extends Exception
{
	public PublicException(String message)
	{
		super(message);
	}
}
