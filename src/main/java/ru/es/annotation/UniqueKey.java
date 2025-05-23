package ru.es.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UniqueKey
{
	// если TRUE, то для SQL поле будет считаться как autoIncreasement
	// если поле == Integer.MIN_VALUE, то в базу данных запишется значение autoIncreasement
	boolean autoIncrement() default false;
}
