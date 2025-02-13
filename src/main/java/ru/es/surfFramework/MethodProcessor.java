package ru.es.surfFramework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public interface MethodProcessor
{
	void process(Method m, Object newInstance, Annotation annotationClass);
}
