package ru.es.exceptions;

import ru.es.util.ESProperties;

import java.io.IOException;

public class PropFileNotFoundException extends IOException
{
	public final ESProperties defaultProps;

	public PropFileNotFoundException(ESProperties defaultProps)
	{
		this.defaultProps = defaultProps;
	}
}
