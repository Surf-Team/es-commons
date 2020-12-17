package ru.es.util;

import java.util.Locale;

public class Environment
{
    public enum OSType
    {
        Windows32,
        Windows64,
        WindowsUnk,
        MacOS,
        Unix,
        Unknown
    }

    private static OSType detectedOS;

    public static boolean allowDebug = false;

    static
    {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            detectedOS = OSType.MacOS;
        } else if (OS.indexOf("win") >= 0)
        {
            String archName =  System.getProperty("sun.arch.data.model");
            if (archName.contains("32"))
                detectedOS = OSType.Windows32;
            else if (archName.contains("64"))
                detectedOS = OSType.Windows64;
            else
                detectedOS = OSType.WindowsUnk;

        } else if (OS.indexOf("nux") >= 0) {
            detectedOS = OSType.Unix;
        } else {
            detectedOS = OSType.Unknown;
        }
    }

    public static OSType getOS()
    {
        return detectedOS;
    }

    public static boolean isWindows()
    {
        return detectedOS == OSType.Windows64 || detectedOS == OSType.Windows32 || detectedOS == OSType.WindowsUnk;
    }

    public static boolean isMac()
    {
        return detectedOS == OSType.MacOS;
    }

    public static boolean isUnix()
    {
        return detectedOS == OSType.Unix;
    }

    public static String getOSFullName()
    {
        return System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
    }
}
