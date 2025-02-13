package ru.es.surfFramework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public interface FieldProcessor
{
	void process(Field f, Object newInstance, Annotation annotationClass, SurfFramework framework) throws Exception;
}
