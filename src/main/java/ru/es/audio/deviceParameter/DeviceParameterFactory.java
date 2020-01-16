package ru.es.audio.deviceParameter;

public class DeviceParameterFactory
{
    public final ParameterInfo parameterInfo;
    public final float defaultValue;

    public DeviceParameterFactory(float defaultValue, ParameterInfo parameterInfo)
    {
        this.parameterInfo = parameterInfo;
        this.defaultValue = defaultValue;
    }

    public DeviceValueProperty createValue()
    {
        return new DeviceValueProperty(defaultValue);
    }
}
