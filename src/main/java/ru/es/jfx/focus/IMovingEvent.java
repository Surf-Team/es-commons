package ru.es.jfx.focus;

public interface IMovingEvent
{
    void moveUp(Indexed indexed);

    void moveDown(Indexed indexed);

    Indexed getNext(Indexed indexed);

    Indexed getPrevius(Indexed indexed);
}
