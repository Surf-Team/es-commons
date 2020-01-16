package ru.es.lang;

/**
 * Created by saniller on 22.03.2017.
 */
public class MultiReturn<A, B>
{
    A a;
    B b;

    public MultiReturn(A a, B b)
    {
        this.a = a;
        this.b = b;
    }

    public A getA()
    {
        return a;
    }

    public B getB()
    {
        return b;
    }
}
