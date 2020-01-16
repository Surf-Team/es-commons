package ru.es.util;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created with IntelliJ IDEA.
 * User: Saniller
 * Date: 14.09.14
 * Time: 2:49
 * To change this template use File | Settings | File Templates.
 */
public class SkinUtils
{
    public static Color buttonGray = new Color(128, 128, 128);
    public static Color buttonGrayLight = new Color(165, 165, 165);
    public static Color buttonDoubleCoral = new Color(255, 94, 40);
    public static Color red = new Color(255, 0, 0);
    public static Color buttonPurple = new Color(159, 81, 255);      // new Color(0x8A, 0x2B, 0xE2);
    public static Color buttonPurpleLight = new Color(0xCA, 0x9B, 0xF2);
    public static Color buttonContextMenu = new Color(102, 153, 255);
    public static Color buttonCoralLight = new Color(213, 157, 136);

    public static Color greenButton = new Color(0, 190, 116);

    public static Color textBlack = new Color(0, 0, 0);
    public static Color textWhite = new Color(255, 255, 255);

    public static Color scrollBorder = new Color(110, 110, 110);
    public static Color lightPanel = new Color(90, 90, 90);
    public static Color panel = new Color(70, 70, 70);
    public static Color panel2 = new Color(50, 50, 50);
    public static Color backPanel = new Color(55, 55, 55);

    public static Color transperent = new Color(0, 0, 0, 0);

    public static BufferedImage copyImage;

    static
    {
        // решение не доделанное
        copyImage = new BufferedImage(30, 30, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D imgGraphics = (Graphics2D) copyImage.getGraphics();
        imgGraphics.setColor(textBlack);
        imgGraphics.drawRoundRect(0,0,15,15, 8,8);
    }
}
