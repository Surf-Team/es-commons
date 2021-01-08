package ru.es.reflection;

import java.util.Map;

public interface IHandleManager<T>
{
    void acceptTemporaryes();

    void createNewTemporaryes();

    void checkRegisterTemporary(Class<?> clazz);

    int getSize();
}
