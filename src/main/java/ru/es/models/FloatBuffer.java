package ru.es.models;

public interface FloatBuffer
{
    int limit();

    public void position(int pos);

    public float getFloat();

    void putFloat(float f);
}
