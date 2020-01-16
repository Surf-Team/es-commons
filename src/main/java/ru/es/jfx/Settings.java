package ru.es.jfx;

import java.util.HashMap;

public abstract class Settings extends ESXmlObject
{
    HashMap<String, SettingValue> map = new HashMap<>();

    public<T> SettingValue<T> getSetting(String name, T defaultValue)
    {
        assert allowSaveType(defaultValue);

        SettingValue<T> ret = map.get(name);
        if (ret == null)
        {
            ret = new SettingValue<T>(defaultValue, name);
            map.put(name, ret);
        }
        return ret;
    }


}
