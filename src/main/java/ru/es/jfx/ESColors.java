package ru.es.jfx;

import ru.es.util.ESFXUtils;
import javafx.scene.paint.Color;

/**
 * Created by saniller on 30.10.2016.
 */
public class ESColors
{
    public static Color blackTransparentLighter = Color.rgb(0,0,0, 0.4);
    public static Color accentColor = Color.web("#ffe300");
    public static Color accentColorDarker = Color.web("#ccb400");
    public static Color purpleLight = Color.rgb(0xCA, 0x9B, 0xF2);
    public static Color purple = Color.rgb(159, 81, 255);    // old Color.rgb(0x8A, 0x2B, 0xE2);
    public static Color purpleDark = Color.rgb(0x8A, 0x2B, 0xE2);
    public static Color coral = Color.rgb(242, 121, 76); // stable: Color.rgb(242, 121, 76);
    public static Color coral2 = Color.rgb(242, 121, 76); // stable: Color.rgb(242, 121, 76);
    public static Color coralSmothest = Color.rgb(255, 225, 206);
    public static Color virtualPianoPaper = Color.rgb(231, 224, 219);

    public static Color hoverElementColor = Color.rgb(255,255,255,0.03);
    public static Color hoverControlColor = Color.rgb(255,75,0, 0.15);

    public static Color green = Color.rgb(0, 190, 116);

    public static Color virtualPianoBlackNoteColor = Color.rgb(150, 150, 150);
    public static Color virtualPianoNoneNamesColor = Color.rgb(30,30,30);
    public static Color virtualPianoNoneNamesColorOutOfGamma = Color.rgb(120,120,120);
    public static Color virtualPianoCarcasColor = Color.rgb(120,120,120);

    public static Color patternExistsColor = coralSmothest;
    public static Color patternExistsColor2 = Color.rgb(225, 225, 225);

    public static Color volumeLimit = Color.RED;

    public static Color trianglePlay = purple;

    public static Color elementBackgroundMuted = Color.rgb(0,0,0, 0.5);
    public static Color elementBorder = Color.rgb(0,0,0);
    public static Color paper = Color.rgb(231,226,224);     //  Color.rgb(221,216,214);
    public static Color paperSQ = Color.rgb(231,226,224);     //  Color.rgb(221,216,214);
    public static Color paperSelectedRow = Color.rgb(245,245,245);
    public static Color gridColor = Color.rgb(190,190,190);
    public static Color gridColorSQ = gridColor;
    public static Color paperSelectionBackColor = Color.rgb(255,231,221);//Color.rgb(237,218,248);  //Color.rgb(182,241,241);
    public static Color startSelectionTickColor = paperSelectionBackColor;//Color.rgb(243, 209, 177);
    public static Color timeHeaderColor = paper.deriveColor(1,1,1.05,1);
    public static Color timeHeaderTextColor = Color.rgb(150,150,150);
    public static Color timeHeaderDelimsColor = Color.rgb(150,150,150);
    public static Color timeHeaderSelection = paperSelectionBackColor;//Color.rgb(160, 160, 255, 0.5);
    public static Color pianoRollColor = timeHeaderColor;
    public static Color pianoRollColorBlack = pianoRollColor.deriveColor(1,1,0.8,1);
    public static Color notesListNoteNames = timeHeaderTextColor;

    public static Color timePlayingLineColor = Color.rgb(0xCA, 0x9B, 0xF2);
    public static Color timePlayingLineColor2 = Color.GRAY;

    //public static Color paperSelectionBackColor = Color.rgb(255,110,110, 0.15);//Color.rgb(237,218,248);  //Color.rgb(182,241,241);
    static
    {
        paper = ESFXUtils.convertAWTColorToJFX(new java.awt.Color(195, 195-4, 195-6));   // 135,133,132
        paperSQ = paper;
        coral = Color.rgb(255, 155, 56);
        coral2 = coral;//Color.rgb(255, 174, 87);

        paperSelectedRow =
                paper.deriveColor(1, 1, 1.15,1);

        // 1.3 for dark
        gridColor = paper.deriveColor(1, 1, 0.8,1);
        gridColorSQ = gridColor;


        timeHeaderColor = paper.deriveColor(1,1,1.1,1);
        timeHeaderTextColor = Color.BLACK;
        timeHeaderDelimsColor = gridColor;   // dark skin: gray85
        timeHeaderSelection = Color.rgb(160, 160, 255, 0.6);

        timePlayingLineColor = Color.rgb(160,0,255);


        virtualPianoPaper = paper;
        virtualPianoBlackNoteColor = virtualPianoPaper.deriveColor(1,1,0.8,1);
        virtualPianoCarcasColor = timeHeaderDelimsColor;
        virtualPianoNoneNamesColor = timeHeaderTextColor;
        virtualPianoNoneNamesColorOutOfGamma = timeHeaderTextColor;
    }

    static
    {
        // new dark skin
        timeHeaderColor = Color.rgb(64,64,64);
        timeHeaderTextColor = Color.rgb(160,160,160);
        timeHeaderDelimsColor = Color.rgb(76,76,76);
        pianoRollColor = Color.rgb(76,76,76);
        pianoRollColorBlack = Color.rgb(60,60,60);

        paper = Color.rgb(59,59,59);
        paperSQ = Color.rgb(160,160,160);
        gridColorSQ = Color.rgb(144,144,144);
        gridColor = Color.rgb(50,50,50);
        notesListNoteNames = Color.rgb(140,140,140);
        paperSelectedRow = ESColors.paper;//ESColors.paper.deriveColor(0,1,1.2,1);

        paperSelectionBackColor = Color.rgb(255,180,0, 0.12);
        startSelectionTickColor = paperSelectionBackColor;
        timeHeaderSelection = paperSelectionBackColor;

        timePlayingLineColor = Color.rgb(255,246,5);

    }


    public static Color selectionColor = Color.rgb(40,237,237);
    public static Color selectionColor2 = Color.rgb(20,207,207);
    public static Color selectionColo3 = Color.rgb(0,137,161);

    public static Color selectionTransparent = selectionColor.deriveColor(1.0, 1.0, 1.0, 0.4);
    public static Color selectionCoralTransparent = coral.deriveColor(1.0, 1.0, 1.0, 0.4);


    public static String toWeb(Color color)
    {
        if (color == Color.TRANSPARENT)
            return "transparent";

        String r = Integer.toString((int) (color.getRed()*255), 16);
        if (r.length() == 1)
            r = "0"+r;

        String g = Integer.toString((int) (color.getGreen()*255), 16);
        if (g.length() == 1)
            g = "0"+g;

        String b = Integer.toString((int) (color.getBlue()*255), 16);
        if (b.length() == 1)
            b = "0"+b;

        return "#"+r+g+b;
    }
}
