package ru.es.annotation;

import ru.es.lang.VariableProvider;

public class AnnotatedParserSettings
{
	public DependencyManager dependencyManager = null;
	public VariableProvider variableProvider = null;
	public boolean allowParseSuperclass = false;
	public boolean allowParseSubElement = true;
	public boolean fieldNotFoundCheck = false;
}
