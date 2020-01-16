package ru.es.jfx.fxml;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

public class ESFXMLLoader
{
    public final Pane control;
    public final FXMLLoader loader;   // get controller from there

    public ESFXMLLoader(File file) throws IOException // new File("./fxml/Index.fxml")
    {
        loader = new FXMLLoader();
        loader.setLocation(file.toURI().toURL());
        control = loader.load();
    }
}
