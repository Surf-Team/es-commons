package ru.es.reflection;

import java.util.Map;

public interface IHandleManager<T>
{
    void acceptTemporaries();

    void createNewTemporaries();

    void checkRegisterTemporary(Class<?> clazz);

    int getSize();
}
