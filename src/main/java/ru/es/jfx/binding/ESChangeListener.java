package ru.es.jfx.binding;

import javafx.beans.value.ChangeListener;

/**
 * Created by saniller on 07.08.2016.
 */
public abstract class ESChangeListener<T> implements ChangeListener<T>
{
    // разница этого листенера и стандартного в том, что здесь можно разшерать вызов метода changed одновременно с методом addListener
    // добавляешь листенер - сразу вызывается onChange. Это бывает удобно во многих случаях
    // если true - то при добавлении этого листенера (addListener) к ESProperty сразу вызовется метод changed
    // если false - то метод вызываться не будет (т.е. будет действовать как обычный ChangeListener)
    // если объявляем ChangeListener при инициализации полей классов, то желательно ставить false.
    public ESChangeListener(boolean updateOnInit)
    {
        this.updateOnInit = updateOnInit;
    }

    boolean updateOnInit;

    public boolean isUpdateOnInit()
    {
        return updateOnInit;
    }
}
