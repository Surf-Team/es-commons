package ru.es.net;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.es.log.Log;

import java.io.IOException;
import java.io.OutputStream;

public abstract class HttpHandlerImpl implements HttpHandler
{
    @Override
    public final void handle(HttpExchange httpExchange) throws IOException
    {
        try
        {
            handleImpl(httpExchange);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
            try
            {
                httpExchange.sendResponseHeaders(HttpCode.INTERNAL_SERVER_ERROR.code, 0);
                OutputStream os = httpExchange.getResponseBody();
                os.close();
            }
            catch (IOException ex)
            {
                e.printStackTrace();
            }
        }
    }

    public abstract void handleImpl(HttpExchange httpExchange);
}
