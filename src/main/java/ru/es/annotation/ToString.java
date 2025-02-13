package ru.es.annotation;

import ru.es.lang.ESStringConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// говорит о том что данное значение можно преобразовать в string через toString() или спарсить через конструктор с аргументом String
// либо можно использовать свой StringConverter
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ToString
{
	// если нужно использовать свой кастомный конвертер
	Class<? extends ESStringConverter> value() default ESStringConverter.class;
}

