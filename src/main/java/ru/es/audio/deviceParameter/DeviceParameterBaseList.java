package ru.es.audio.deviceParameter;

public class DeviceParameterBaseList<T> extends DeviceParameterBase
{
    public final DeviceListParameter<T> listParameter;
    public final DeviceParameterListFactory<T> factory;

    public DeviceParameterBaseList(DeviceParameterListFactory<T> factory)
    {
        super(factory.parameterInfoList, null);
        this.factory = factory;
        listParameter = factory.createValue();
        value = listParameter.floatValueWrapper;
        defaultValue = value.value();
    }
}
