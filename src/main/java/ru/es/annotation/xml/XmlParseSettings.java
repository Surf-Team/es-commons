package ru.es.annotation.xml;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlParseSettings
{
	boolean allowParse() default true;
	boolean allowDefaultValue() default false; // если true, то не вызовет ошибку в случае если элемент не найден
}
