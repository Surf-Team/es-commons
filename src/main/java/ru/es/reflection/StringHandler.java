package ru.es.reflection;

/**
 * Created by saniller on 26.07.2015.
 */
public interface StringHandler extends Handler<String>
{
    @Override
    String[] getCommands();
}
