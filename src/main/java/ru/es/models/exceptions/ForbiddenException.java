package ru.es.models.exceptions;

import java.io.File;
import java.net.URL;

public class ForbiddenException extends Exception
{
    public ForbiddenException(File f)
    {
        super("File is forbidden: " + f.getAbsolutePath());
    }
    public ForbiddenException(URL f)
    {
        super("forbidden: " + f.toString());
    }
    public ForbiddenException(String f)
    {
        super("forbidden str: " + f.toString());
    }
}
