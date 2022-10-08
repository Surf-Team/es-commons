package ru.es.surfFramework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// интерфейс или абстрактный класс, элементы которого будут собраны в список
// + каждый наследующий класс станет синглтоном
// see GveFramework.getObjects
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Collect
{
}
