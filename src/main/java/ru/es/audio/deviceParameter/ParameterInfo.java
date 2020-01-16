package ru.es.audio.deviceParameter;

import ru.es.jfx.binding.ESProperty;
import ru.es.util.StringConverters;
import javafx.util.StringConverter;

/**
 * Created by saniller on 25.12.2016.
 * Параметр устройства IDevice
 */
public class ParameterInfo
{
    // ######################################
    // ############## PUBLIC ################
    // ######################################

    public ESProperty<String> name = new ESProperty<String>("noname");  // видимое имя (например "громкость")
    public ESProperty<String> shortName;
    public final DeviceValueProperty min = new DeviceValueProperty(0.0f);
    public final DeviceValueProperty max = new DeviceValueProperty(1.0f);
    public final ESProperty<Double> step = new ESProperty<>(0.01); // минимальный шаг при изменении (например 0.01 или 1.0) или кратность
    public final ESProperty<StringConverter<Number>> valueStringConverter = new ESProperty<StringConverter<Number>>(null);
    public float defaultValue = 0; //todo только начинаем это вводить.

    public double regulatorDivider = 1.0;

    // index - уникальный номер для текущего устройства
    public ParameterInfo(String name, double min, double max, double step, StringConverter<Number> valueStringConverter)
    {
        this.name.set(name);
        shortName = this.name;
        this.min.set((float) min);
        this.max.set((float) max);
        this.step.set(step);
        this.valueStringConverter.set(valueStringConverter);
    }

    public ParameterInfo(String name, String shortName, double min, double max, double step, StringConverter<Number> valueStringConverter)
    {
        this(name, min, max, step, valueStringConverter);
        this.shortName = new ESProperty<>(shortName);
    }

    public ParameterInfo(String name, double min, double max, double step, StringConverter<Number> valueStringConverter, double regulatorDivider)
    {
        this.name.set(name);
        shortName = this.name;
        this.min.set((float) min);
        this.max.set((float) max);
        this.step.set(step);
        this.valueStringConverter.set(valueStringConverter);
        this.regulatorDivider = regulatorDivider;
    }

    public ParameterInfo(String name, double min, double max, double step, double defaultValue)
    {
        this(name, min, max, step);
        this.defaultValue = (float) defaultValue;
    }

    // вариант с автоматическим стринг конвертером (округляем до значения step).
    public ParameterInfo(String name, double min, double max, double step)
    {
        this.name.set(name);
        shortName = this.name;
        this.min.set((float) min);
        this.max.set((float) max);
        this.step.set(step);
        this.valueStringConverter.set(StringConverters.getAutoStringConverter(step));
    }

    public ParameterInfo(ESProperty<String> name, double min, double max, double step, StringConverter<Number> valueStringConverter)
    {
        this.name = name;
        shortName = this.name;
        this.min.set((float) min);
        this.max.set((float) max);
        this.step.set(step);
        this.valueStringConverter.set(valueStringConverter);
    }


    //for normalized
    public ParameterInfo(String name)
    {
        this(name, 0, 1, 0.00001);
    }

    //for normalized
    public ParameterInfo(String name, StringConverter<Number> valueStringConverter)
    {
        this(name, 0, 1, 0.00001, valueStringConverter);
    }

    //for normalized
    public ParameterInfo(String name, StringConverter<Number> valueStringConverter, float defaultValue)
    {
        this(name, 0, 1, 0.00001, valueStringConverter);
        this.defaultValue = defaultValue;
    }

}
