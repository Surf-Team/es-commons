package ru.es.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// может использоваться только для массивов со статичной длиной, чтобы разметить названия каждого элемента
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ArrayInfo
{
	String[] elementNames();
}
