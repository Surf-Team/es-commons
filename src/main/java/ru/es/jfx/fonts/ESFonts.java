package ru.es.jfx.fonts;

import javafx.scene.text.Font;
import ru.es.log.Log;

import java.io.InputStream;

/**
 * Created by saniller on 16.01.2017.
 */
public class ESFonts
{
    public static void load()
    {
        // при обращении просто инициализирует класс, и загружает статичные данные
    }

    public static Font simpleFont = new Font("Arial Bold", 12);
    public static Font smallFont = new Font("Arial Bold", 11);
    public static Font notBold11 = new Font("Arial", 11);
    public static Font font10 = new Font("Arial Bold", 10);

    // Visitor TT2 BRK
    //public static Font lowHeightNumbers = loadFontFromFontsFolder("resources/Visitor TT2 BRK.ttf");

    // Elevate PERSONAL USE ONLY
    //public static Font engCursive = loadFontFromFontsFolder("resources/Elevate PERSONAL USE ONLY.ttf");

    // Advanced Pixel LCD-7
    //public static Font digital = loadFontFromFontsFolder("resources/Advanced Pixel LCD-7.ttf");

    // digital-7 Mono
    //public static Font digitalBase = loadFontFromFontsFolder("resources/digital.ttf");

    // AA American Captain - used in ESPRESSO STUDIO logo
    public static Font americanCapitan = loadFontFromFontsFolder("/ru/es/jfx/fonts/resources/AA American Captain.ttf");
    public static Font segoeuib = loadFontFromFontsFolder("/ru/es/jfx/fonts/resources/segoeuib.ttf");

    // Share Tech Mono
    //public static Font shareTechMono = loadFontFromFontsFolder("resources/ShareTechMono-Regular.ttf");

    // FontAwesome
    //public static Font fontAwesomeZ = loadFontFromFontsFolder("fontawesome-webfont.ttf");

    public static Font fontAwesomeF = loadFontFromFontsFolder("/ru/es/jfx/fonts/resources/fontawesome-webfont.ttf");
    public static String fontAwesomeName = "FontAwesome Regular";
    /*public static FontAwesome fontAwesome;
    static
    {
        InputStream is = ESFXUtils.getResourceAsStream("/es/jfx/fonts/resources/fontawesome-webfont.ttf");
        if (is == null)
            throw new RuntimeException();

        fontAwesome = new FontAwesome(is);
    } */

    static
    {
        // is polyform key
        System.setProperty("systemFont", "NOdsgonkokdsg3y");
    }

    public static Font loadFontFromFontsFolder(String fontName)
    {
        Font f2;
        try
        {
            //ClassLoader classLoader = ESFonts.class.getClassLoader();

            InputStream is = ESFonts.class.getResourceAsStream(fontName);
            //InputStream is = ESFonts.class.getClassLoader().getResourceAsStream(fontName);

            if (is == null)
                throw new Exception("Input Stream Is null (1)");

            Font.loadFont(is, 12);

            //Font f = Toolkit.getToolkit().getFontLoader().loadFont(is, 12);
            is = ESFonts.class.getResourceAsStream(fontName);

            if (is == null)
                throw new Exception("Input Stream Is null (1)");

            f2 = Font.loadFont(is, 18);
            //Log.info("Loaded font-family: " + f2.getName());
        }
        catch (Exception e)
        {
            Log.warning("Error when loading font "+fontName);
            e.printStackTrace();
            return null;
        }
        
        return f2;
    }
}
