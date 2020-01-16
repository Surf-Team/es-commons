package ru.es.jfx.events;

import javafx.application.Platform;

import java.awt.*;

/**
 * Created by saniller on 04.05.2017.
 */
public class ESCursorUtils
{
    static Robot robot;

    public static void moveCursor(double screenX, double screenY) {
        Platform.runLater(() -> {
            try
            {
                if (robot == null)
                    robot = new Robot();

                robot.mouseMove((int) screenX, (int) screenY);
            }
            catch (AWTException e)
            {
                e.printStackTrace();
            }
        });
    }
}
