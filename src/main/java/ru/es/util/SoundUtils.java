package ru.es.util;

import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 23.01.15
 * Time: 1:38
 * To change this template use File | Settings | File Templates.
 */
public class SoundUtils
{
    public static void playWindowsAlert()
    {
        Runnable sound2 = (Runnable)Toolkit.getDefaultToolkit().getDesktopProperty("win.sound.exclamation");
        if(sound2 != null)
        {
            sound2.run();
        }
    }
}
