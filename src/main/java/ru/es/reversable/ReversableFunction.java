package ru.es.reversable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saniller on 19.04.2017.
 */
public abstract class ReversableFunction implements IReversableFunction
{
    // для сохранения ссылок на объекты, которые не нужно удалять, пока эта функция не уйдёт из истории
    public List<Object> weakObjects = new ArrayList<>();
}
