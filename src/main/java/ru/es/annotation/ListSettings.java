package ru.es.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ListSettings
{
	String elementsName(); // будет считать списом элементы в корне с этим именем
	Class objectsClass();
	String subElement() default ""; // если != empty, то будет брать элементы из суб-элемента
}
