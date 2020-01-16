package ru.es.audio.deviceParameter;

public interface IDeviceParameter
{
    ParameterInfo getInfo();
    DeviceValueProperty valueProperty();
    void setValue(float f);
    void setByUser(float f);
    float getDefaultValue();
}
