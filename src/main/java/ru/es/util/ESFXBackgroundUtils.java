package ru.es.util;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

public class ESFXBackgroundUtils
{
    // percent size: 0.5 = 50%
    public static Background getLogoImage(Image image, double percentSize)
    {
        return new Background(new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(percentSize,percentSize,true,true,false, false)));
    }

    public static Background getLogoImage(Image image, double w, double h)
    {
        return new Background(new BackgroundImage(image,
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(w,h,false,false,false, false)));
    }
}
