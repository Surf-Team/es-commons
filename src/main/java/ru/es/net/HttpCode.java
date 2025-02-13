package ru.es.net;

public enum HttpCode
{
    CONTINUE(100),
    SUCCESS(200),
    REDIRECT_TEMP(302),
    BAD_REQUEST(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(505);

    public int code;
    HttpCode(int i)
    {
        code = i;
    }
}
