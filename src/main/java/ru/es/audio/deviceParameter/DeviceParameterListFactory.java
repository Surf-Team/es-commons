package ru.es.audio.deviceParameter;

public class DeviceParameterListFactory<T>
{
    public final T defaultValue;
    public final ParameterInfoList<T> parameterInfoList;

    public DeviceParameterListFactory(T defaultValue, ParameterInfoList<T> parameterInfoList)
    {
        this.defaultValue = defaultValue;
        this.parameterInfoList = parameterInfoList;
    }

    public DeviceListParameter<T> createValue()
    {
        return new DeviceListParameter<T>(defaultValue, parameterInfoList);
    }
}
