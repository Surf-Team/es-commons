package ru.es.audio.deviceParameter;

import ru.es.jfx.binding.ESProperty;

public class DeviceListParameter<T>
{
    public final ParameterInfo info;
    public final ESProperty<T> value;
    public final ESCollectionIndexFloatProperty<T> floatValueWrapper;

    public DeviceListParameter(T defaultValue, ParameterInfoList<T> parameterInfo)
    {
        info = parameterInfo;
        value = new ESProperty<>(defaultValue);
        floatValueWrapper = new ESCollectionIndexFloatProperty<T>(value, parameterInfo.list);
    }

    public DeviceListParameter(T defaultValue, ESProperty<T> value, ParameterInfoList<T> parameterInfo)
    {
        info = parameterInfo;
        this.value = value;
        floatValueWrapper = new ESCollectionIndexFloatProperty<T>(value, parameterInfo.list);
    }
}
