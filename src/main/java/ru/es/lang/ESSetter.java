package ru.es.lang;

import java.sql.SQLException;

/**
 * Created by saniller on 11.09.2015.
 */
public interface ESSetter<T>
{
    void set(T newValue);
}
