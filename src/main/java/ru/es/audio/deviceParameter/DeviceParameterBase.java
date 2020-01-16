package ru.es.audio.deviceParameter;


public class DeviceParameterBase implements IDeviceParameter
{
    public DeviceValueProperty value;
    public final ParameterInfo parameterInfo;
    protected float defaultValue;

    public DeviceParameterBase(DeviceParameterFactory factory)
    {
        this.parameterInfo = factory.parameterInfo;
        this.value = factory.createValue();
        this.defaultValue = factory.defaultValue;
    }

    public DeviceParameterBase(ParameterInfo info, DeviceValueProperty value)
    {
        this.parameterInfo = info;
        this.value = value;
    }

    @Override
    public DeviceValueProperty valueProperty()
    {
        return value;
    }

    @Override
    public void setValue(float f)
    {
        value.set(f);
    }

    @Override
    public void setByUser(float f)
    {
        value.setByUser(f);
    }

    @Override
    public ParameterInfo getInfo()
    {
        return parameterInfo;
    }

    @Override
    public float getDefaultValue()
    {
        return defaultValue;
    }
}
