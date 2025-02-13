package ru.es.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface DynamicType
{
	// аннотация говорит о том что класс может иметь наследников, и поэтому при сериализации нужно сохранить название класса
	// это позволит создать правильный класс при парсинге
	String fieldName() default "__class__";
}
