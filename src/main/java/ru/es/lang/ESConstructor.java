package ru.es.lang;

/**
 * Created by saniller on 30.06.2016.
 */
public interface ESConstructor<NewObject, FromThis>
{
    NewObject createObject(FromThis fromThis);
}
