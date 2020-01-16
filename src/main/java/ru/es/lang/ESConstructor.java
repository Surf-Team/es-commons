package ru.es.lang;

/**
 * Created by saniller on 30.06.2016.
 */
public abstract class ESConstructor<NewObject, FromThis>
{
    public abstract NewObject createObject(FromThis... fromThis);
}
