package ru.es.util;

import ru.es.lang.FloatCall;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;

/**
 * Created by saniller on 02.03.2017.
 */
public class StringConverters
{
    public static StringConverter<Number> onOffConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            return object.intValue() == 1 ? "On" : "Off";
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };
    public static StringConverter<Number> intStringConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            return ""+object.intValue();
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };

    // конвертируем в целые числа
    public static StringConverter<Number> createToIntConverter(String valueName, int visibleOffset)
    {
        return new StringConverter<Number>()
        {
            @Override
            public String toString(Number object)
            {
                String ret = (object.intValue() + visibleOffset) + "";

                if (ret.contains("."))
                    ret = (ret).substring(0, ret.indexOf("."));

                if (!valueName.isEmpty())
                    return ret + " "+valueName;
                else
                    return ret;
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }

    public static StringConverter<Number> getAutoStringConverter(double step)
    {
        if (step <= 0.001)
            return threeFloatsStringConverter;
        else if (step < 1)
            return twoFloatsStringConverter;
        else
            return zeroIntStringConverter;
    }

    public static StringConverter<Number> createBooleanNormalizedStringConverter(String onWord, String offWord)
    {
        return new StringConverter<Number>() {
            @Override
            public String toString(Number object)
            {
                return object.doubleValue() == 1 ? onWord : offWord;
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }

    public static StringConverter<Number> createToTwoFloatsConverter(String valueName, float visibleOffset)
    {
        return new StringConverter<Number>()
        {
            @Override
            public String toString(Number object)
            {
                float val = object.floatValue()+visibleOffset;
                String ret = StringUtils.getNumberWithFixedSizeAfterDot(val, 2);

                /**int a = (int) (object.floatValue() * 100);
                float b = a / 100f + +visibleOffset;**/

                if (!valueName.isEmpty())
                    return ret + " "+valueName;
                else
                    return ret+ " ";
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }

    public static StringConverter<Number> createToTwoFloatsConverter(String valueName, FloatCall floatCall)
    {
        return new StringConverter<Number>()
        {
            @Override
            public String toString(Number object)
            {
                float val = floatCall.call(object.floatValue());
                String ret = StringUtils.getNumberWithFixedSizeAfterDot(val, 2);

                /**int a = (int) (object.floatValue() * 100);
                float b = a / 100f + +visibleOffset;**/

                if (!valueName.isEmpty())
                    return ret + " "+valueName;
                else
                    return ret+ " ";
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }


    public static StringConverter<Number> createFloatsConverter(String valueName, float visibleOffset, int zeroCount)
    {
        return new StringConverter<Number>()
        {
            @Override
            public String toString(Number object)
            {
                float val = object.floatValue()+visibleOffset;
                String ret = StringUtils.getNumberWithFixedSizeAfterDot(val, zeroCount);

                /**int a = (int) (object.floatValue() * 100);
                float b = a / 100f + +visibleOffset;**/

                if (!valueName.isEmpty())
                    return ret + " "+valueName;
                else
                    return ret+ " ";
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }


    // для списков
    public static<T> StringConverter<Number> createListItemConverter(List<T> items)
    {
        return new StringConverter<Number>()
        {
            @Override
            public String toString(Number object)
            {
                //int index = object.intValue();
                //if (index == -1)
                    ///return "None";

                return items.get(object.intValue()).toString();
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }

    // для array
    public static<T> StringConverter<Number> createListItemConverter(T[] items)
    {
        return new StringConverter<Number>()
        {
            @Override
            public String toString(Number object)
            {
                //int index = object.intValue();
                //if (index == -1)
                    ///return "None";

                return items[object.intValue()].toString();
            }

            @Override
            public Number fromString(String string)
            {
                return null;
            }
        };
    }

    public static StringConverter<Number> minus64IntConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            return Integer.toString(object.intValue() - 64);
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };

    public static StringConverter<Number> zeroIntStringConverter = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            return Integer.toString(object.intValue());
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };

    public static StringConverter<Number> twoFloatsStringConverter = createToTwoFloatsConverter("", 0);
    public static StringConverter<Number> percent127 = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            float val = object.floatValue();

            val = (float)(val/127.0*100.0);

            return ""+StringUtils.getNumberWithFixedSizeAfterDot(val, 1)+"%";
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };
    public static StringConverter<Number> percent127m64 = new StringConverter<Number>() {
        @Override
        public String toString(Number object)
        {
            float val = object.floatValue();

            // -64 -> 63
            val -= 64;
            if (val < 0)
                val = (float) (val/64.0*100.0);
            else
                val = (float) (val/63.0*100.0);

            return ""+StringUtils.getNumberWithFixedSizeAfterDot(val, 1)+"%";
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };
    public static StringConverter<Number> threeFloatsStringConverter = createFloatsConverter("", 0, 3);

    public static StringConverter<Number> noteStringConverter = new StringConverter<Number>()
    {
        @Override
        public String toString(Number object)
        {
            return Words.getNoteName(object.intValue(), true);
        }

        @Override
        public Number fromString(String string)
        {
            return null;
        }
    };

    public static StringConverter<File> fileAbsolutePathConverter = new StringConverter<File>() {
        @Override
        public String toString(File object)
        {
            return object.getAbsolutePath();
        }

        @Override
        public File fromString(String string)
        {
            return null;
        }
    };
}
