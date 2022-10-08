package ru.es.surfFramework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// переменная будет жить до выключения приложения, даже после обновления класса новой версией класса
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface VariableUntilShutdown
{
}
