package ru.es.audio;

import com.allatori.annotations.ControlFlowObfuscation;

public class FloatValue implements FloatGetter
{
    float value;

    public FloatValue(float value)
    {
        this.value = value;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float get()
    {
        return value;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void set(float value)
    {
        this.value = value;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float value()
    {
        return value;
    }
}
