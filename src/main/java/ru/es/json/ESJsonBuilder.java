package ru.es.json;

@Deprecated
public class ESJsonBuilder
{
    StringBuilder ret = new StringBuilder();

    public ESJsonBuilder()
    {

    }

    public void addString(String name, String value)
    {
        ret.append(name);
        ret.append(": ");
        ret.append("\"");
        ret.append(value);
        ret.append("\"");
        ret.append(",");
        ret.append(System.lineSeparator());
    }

    public void addBoolean(String name, boolean value)
    {
        ret.append(name);
        ret.append(": ");
        ret.append(value);
        ret.append(",");
        ret.append(System.lineSeparator());
    }

    public void addRawData(String name, String value)
    {
        ret.append(name);
        ret.append(": ");
        ret.append(value);
        ret.append(",");
        ret.append(System.lineSeparator());
    }

    public void addArrayInt(String name, int[] value)
    {
        ret.append(name);
        ret.append(": ");
        ret.append("[ ");
        boolean first = true;
        for (int i : value)
        {
            if (first)
                first = false;
            else
                ret.append(",");

            ret.append(i);
        }
        ret.append("]");
        ret.append(",");
        ret.append(System.lineSeparator());
    }

    public void addArray(String name, ESJsonObject[] value)
    {
        ret.append(name);
        ret.append(": ");
        ret.append("[ ");
        boolean first = true;
        for (ESJsonObject i : value)
        {
            if (first)
                first = false;
            else
                ret.append(",");

            ret.append(i.toJson());
            ret.append(System.lineSeparator());
        }
        ret.append("]");
        ret.append(",");
        ret.append(System.lineSeparator());
    }

    public void addRootArray(ESJsonObject[] value)
    {
        ret.append("[ ");
        boolean first = true;
        for (ESJsonObject i : value)
        {
            if (first)
                first = false;
            else
                ret.append(",");

            ret.append(i.toJson());
            ret.append(System.lineSeparator());
        }
        ret.append("]");
        ret.append(System.lineSeparator());
    }

    public void addRootArray(String[] value)
    {
        ret.append("[ ");
        boolean first = true;
        for (Object i : value)
        {
            if (first)
                first = false;
            else
                ret.append(",");

            ret.append("\"");
            ret.append(i.toString());
            ret.append("\"");
        }
        ret.append("]");
        ret.append(System.lineSeparator());
    }

    public String getAsObject()
    {
        ret.insert(0, "{");
        ret.append("}");
        return ret.toString();
    }

    public String getAsRaw()
    {
        return ret.toString();
    }
}
