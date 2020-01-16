package ru.es.util;

import java.io.*;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Optional;

/**
 * Created by saniller on 17.07.2017.
 */
public class IOUtils
{
    public static String readResource(Class packageClass, String file) throws IOException
    {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try (Reader in = new InputStreamReader(packageClass.getResourceAsStream(file), "UTF-8"))
        {
            for (; ; )
            {
                int rsz = in.read(buffer, 0, buffer.length);
                if (rsz < 0)
                    break;
                out.append(buffer, 0, rsz);
            }
            return out.toString();
        }
    }

    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static Optional<String> serializeToString(final Serializable object) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Optional.of(Base64.getEncoder().encodeToString(baos.toByteArray()));
        } catch (final IOException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static <T extends Serializable> Optional<T> deserializeFromString(final String objectAsString) {
        final byte[] data = Base64.getDecoder().decode(objectAsString);
        try (final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return Optional.of((T) ois.readObject());
        } catch (final IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static void addLibDir(String dir) throws IOException
    {
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths");
            field.setAccessible(true);
            String[] paths = (String[])field.get(null);
            for (int i = 0; i < paths.length; i++) {
                if (dir.equals(paths[i])) {
                    return;
                }
            }
            String[] tmp = new String[paths.length+1];
            System.arraycopy(paths,0,tmp,0,paths.length);
            tmp[paths.length] = dir;
            field.set(null,tmp);
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + dir);
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }
}
