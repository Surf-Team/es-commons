package ru.es.audio.deviceParameter;

import ru.es.util.StringConverters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ParameterInfoList<T> extends ParameterInfo
{
    public ParameterInfoList(String name, T[] keyModes)
    {
        super(name, 0, keyModes.length-1, 1, StringConverters.createListItemConverter(keyModes));
        list = FXCollections.observableArrayList(keyModes);
    }

    public ParameterInfoList(String name, ObservableList<T> keyModes)
    {
        super(name, 0, keyModes.size()-1, 1, StringConverters.createListItemConverter(keyModes));
        list = keyModes;
    }

    public final ObservableList<T> list;
}
