package ru.es.annotation;

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

	String readAs() default ""; // считать значение из поля, которое не равно названию поля в java
}
