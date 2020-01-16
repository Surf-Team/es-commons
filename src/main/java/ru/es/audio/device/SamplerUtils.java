package ru.es.audio.device;

import java.io.File;

public class SamplerUtils
{
    public final static String[] supportedReadFormats = new String[] { ".wav", ".mp3", ".aif", ".eswave" };

    public static boolean isSupportRead(File file)
    {
        if (file == null)
            return false;

        for (String s : supportedReadFormats)
        {
            if (file.getName().endsWith(s))
                return true;
        }
        return false;
    }
}
