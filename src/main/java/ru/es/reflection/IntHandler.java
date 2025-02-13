package ru.es.reflection;

/**
 * Created by saniller on 27.07.2015.
 */
public interface IntHandler extends Handler<Integer>
{
    @Override
    Integer[] getCommands();
}
