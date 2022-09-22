package ru.es.util;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import ru.es.util.writers.FileUrlWriter;
import ru.es.util.writers.HttpUrlWriter;
import ru.es.util.writers.UrlByteWriter;
import ru.es.log.Log;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by saniller on 17.12.2015.
 */
public class FileUtils
{
    private static final XMLOutputter xmlWriter = new XMLOutputter();
    private static final SAXBuilder xmlReader = new SAXBuilder();
    private static Map<String, UrlByteWriter> protocolWriters = new HashMap<>();

    static
    {
        // set xml default format
        Format xmlFormat = Format.getPrettyFormat();
		xmlFormat.setIndent("\t");
		xmlFormat.setTextMode(Format.TextMode.TRIM);
		xmlWriter.setFormat(xmlFormat);

        FileUtils.addWriter("file", new FileUrlWriter());
        FileUtils.addWriter("http", new HttpUrlWriter());
        FileUtils.addWriter("https", new HttpUrlWriter());
    }


    public static String readFile(String file)
    {
        String ret = "";
        try
        {
            FileInputStream myfile = new FileInputStream(file);
            while (myfile.available() > 0)
                ret += myfile.read();
            myfile.close();
        }
        catch (Exception e)
        {
            Log.warning("Cannot read file " + file);
        }
        return ret;
    }

    @Deprecated
    public static String readFile(File file)
    {
        String ret = "";
        FileInputStream myfile = null;
        try
        {
            myfile = new FileInputStream(file);
            byte[] buffer = new byte[myfile.available()];
            myfile.read(buffer, 0, buffer.length);

            ret = new String(buffer, "utf-8");
        }
        catch (Exception e)
        {
            Log.warning("Cannot read file " + file);
        }
        finally
        {
            if (myfile != null)
                try
                {
                    myfile.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
        return ret;
    }

    public static String readFileDefaultEncode(File file)
    {
        String ret = "";
        FileInputStream myfile = null;
        try
        {
            myfile = new FileInputStream(file);
            byte[] buffer = new byte[myfile.available()];
            myfile.read(buffer, 0, buffer.length);

            ret = new String(buffer);
        }
        catch (Exception e)
        {
            Log.warning("Cannot read file " + file);
        }
        finally
        {
            if (myfile != null)
                try
                {
                    myfile.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
        return ret;
    }


    public static void writeFile(File newFile, String data) throws IOException
    {
        if (!newFile.exists())
        {
            try
            {
                newFile.createNewFile();
            }
            catch (IOException e)
            {
                Log.warning(e.getMessage() + ", file: " + newFile.getAbsolutePath());
                throw e;
            }
        }


        newFile.mkdirs();

        FileOutputStream outputStream = new FileOutputStream(newFile);
        outputStream.write(data.getBytes());
        outputStream.close();
    }

    public static void writeFile(File newFile, byte[] data)
    {
        try
        {
            if (!newFile.exists())
                newFile.createNewFile();

            newFile.mkdirs();

            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            fileOutputStream.write(data, 0, data.length);
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    public static void saveXmlDoc(Element element, File fullPatch) throws IOException
    {
        fullPatch.getParentFile().mkdirs();

        if (!fullPatch.exists())
            fullPatch.createNewFile();

        Document doc = new Document(element);

        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        FileWriter fw = new FileWriter(fullPatch);
        outputter.output(doc, fw);
        fw.close();
    }


    public static void saveXmlDocWideFormat(Element element, File fullPatch) throws IOException
    {
        try
        {
            fullPatch.getParentFile().mkdirs();
        }
        catch (Exception e)
        {

        }

        if (!fullPatch.exists())
            fullPatch.createNewFile();

        Document doc = new Document(element);

        XMLOutputter outputter = new XMLOutputter();

        Format f = Format.getPrettyFormat();
        f.setIndent("\t");
        f.setTextMode(Format.TextMode.TRIM);

        outputter.setFormat(f);
        FileWriter fw = new FileWriter(fullPatch);
        outputter.output(doc, fw);
        fw.close();
    }

    public static void saveXmlDocZipped(Element element, File fullPatch)
    {
        try
        {
            if (!fullPatch.getParentFile().exists())
                fullPatch.getParentFile().mkdirs();


            FileOutputStream f = new FileOutputStream(fullPatch);
            GZIPOutputStream gzipOS = new GZIPOutputStream(f);

            //f.write(doc.toString().getBytes());

            element.detach();

            Document doc = new Document(element);
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            //outputter.setFormat(Format.getPrettyFormat());
            outputter.output(doc, gzipOS);

            gzipOS.close();
            f.close();
        }
        catch (Exception ex)
        {
            Log.warning("Error saving XmlDoc, patch: " + fullPatch.getAbsolutePath());
            ex.printStackTrace();
        }
    }

    public static Element getXmlDocZipped(File file)
    {
        try
        {
            FileInputStream fis = new FileInputStream(file);
            GZIPInputStream gzipInputStream = new GZIPInputStream(fis);

            SAXBuilder parser = new SAXBuilder();

            Document ret = parser.build(gzipInputStream);

            //FileReader fr = new FileReader(file);
            //Document ret = parser.build(fr);
            return ret.getRootElement();
        }
        catch (Exception e)
        {
            Log.warning("Error getXmlXMLDocument: " + file);
            e.printStackTrace();
        }
        return null;
    }

    public static Element getXmlDocument(File file) throws IOException, JDOMException
    {
        try
        {
            SAXBuilder parser = new SAXBuilder();
            FileReader fr = new FileReader(file);
            Document ret = parser.build(fr);
            return ret.getRootElement();
        }
        catch (Exception e)
        {
            Log.warning("Error in file: " + file.getName());
            throw e;
        }
    }

    public static Element getXmlDocument(URL file) throws IOException, JDOMException
    {
        try
        {
            SAXBuilder parser = new SAXBuilder();
            var is = file.openStream();
            Document ret = parser.build(is);
            is.close();
            return ret.getRootElement();
        }
        catch (Exception e)
        {
            Log.warning("Error in file: " + file.getPath());
            throw e;
        }
    }

    public static void renameFIle(File file, File newName)
    {
        file.renameTo(newName);
    }

    public static List<String> getFilesInFolder(File folder, final String formatWithPoint)
    {
        String[] files = folder.list(new FilenameFilter()
        {

            @Override
            public boolean accept(File folder, String name)
            {
                return name.endsWith(formatWithPoint);
            }

        });

        List<String> ret = new ArrayList<>();

        if (files != null)
        {
            for (String fileName : files)
            {
                ret.add(fileName);
            }
        }
        return ret;
    }

    /**
     * public static BufferedImage loadImage(File file)
     * {
     * return ImageUtils.loadImage(file);
     * }
     **/

    public static void copyFile(File src, File dest) throws IOException
    {
        InputStream inStream = null;
        OutputStream outStream = null;

        inStream = new FileInputStream(src);
        outStream = new FileOutputStream(dest);

        byte[] buffer = new byte[1024];

        int length;
        //copy the file content in bytes
        while ((length = inStream.read(buffer)) > 0)
        {
            outStream.write(buffer, 0, length);
            Thread.yield();
        }

        inStream.close();
        outStream.close();
    }

    public static String getFileNameExceptType(File f)
    {
        String name = f.getName();
        if (name.contains("."))
        {
            int index = name.lastIndexOf(".");
            return name.substring(0, index);
        }
        else
            return name;
    }


    public static String getFilePatchNameExceptType(File f)
    {
        String name = f.getAbsolutePath();
        int index = name.lastIndexOf(".");
        if (index == -1)
            return name;

        return name.substring(0, index);
    }


    // максимальная вложенность +2 папки (текущая, следующая, после следующая)
    public static List<File> getAllFilesInFolder(File folder, String endWith) // ".dll"
    {
        List<File> ret = new ArrayList<>();
        File[] files = folder.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                if (pathname.toString().endsWith(endWith) && pathname.isFile())
                    return true;
                else if (pathname.isDirectory())
                {
                    File[] files = pathname.listFiles(new FileFilter()
                    {
                        @Override
                        public boolean accept(File pathname)
                        {
                            if (pathname.toString().endsWith(endWith) && pathname.isFile())
                                return true;
                            else if (pathname.isDirectory())
                            {


                                File[] files = pathname.listFiles(new FileFilter()
                                {
                                    @Override
                                    public boolean accept(File pathname)
                                    {


                                        if (pathname.toString().endsWith(endWith) && pathname.isFile())
                                            return true;

                                        return false;
                                    }
                                });

                                if (files != null)
                                    ret.addAll(ListUtils.arrayToList(files));
                            }
                            return false;
                        }
                    });

                    ret.addAll(ListUtils.arrayToList(files));
                }
                return false;
            }
        });
        ret.addAll(ListUtils.arrayToList(files));
        return ret;
    }

    public static List<File> findFiles(List<String> fileNames, List<File> folders)
    {
        List<File> ret = new ArrayList<>();
        for (File f : folders)
        {
            if (f.exists())
                findInFolder(f, fileNames, ret);
        }
        return ret;
    }

    private static void findInFolder(File folder, List<String> fileNames, List<File> result)
    {
        for (File internal : folder.listFiles())
        {
            if (internal.isDirectory())
            {
                findInFolder(internal, fileNames, result);
            }
            else
            {
                for (String fn : fileNames)
                {
                    if (internal.getName().equals(fn))
                        result.add(internal);
                }
            }
        }
    }

    public static List<File> getAllFoldersIn(File folder, boolean includeRoot)
    {
        List<File> ret = new ArrayList<>();

        if (includeRoot)
            ret.add(folder);

        File[] files = folder.listFiles();
        if (files != null)
        {
            for (File f : files)
            {
                if (f.isDirectory())
                    ret.addAll(getAllFoldersIn(f, true));
            }
        }

        return ret;
    }

    public static byte[] getBytes(File file1) throws IOException
    {
        FileInputStream fis = new FileInputStream(file1);
        byte[] ret = new byte[fis.available()];
        fis.read(ret);
        fis.close();
        return ret;
    }

    public static File getDocumentsRoot()
    {
        Properties sysProperties = System.getProperties();

        String OS = (sysProperties.getProperty("os.name")).toUpperCase();

        if (OS.contains("MAC"))  //todo
        {
            return new File(sysProperties.getProperty("user.home"));
        }
        else if (OS.contains("WIN"))
        {
            return new File(sysProperties.getProperty("user.home") + File.separator + "Documents");
        }
        else
        {
            Log.error("Unknown OSC: " + OS);
            return new File(sysProperties.getProperty("user.home") + File.separator + "Documents");
        }
    }

    public static Properties getPropertiesFile(File file) throws IOException
    {
        Properties ret = new Properties();
        InputStream in = new FileInputStream(file);
        ret.load(in);
        in.close();
        return ret;
    }

    public static Properties loadProperties(URL url, String subPath) throws IOException
    {
        return loadProperties(new URL(url.toString() + subPath));
    }

    public static Properties loadProperties(URL url) throws IOException
    {
        Properties ret = new Properties();
        InputStream in = url.openStream();
        ret.load(in);
        in.close();
        return ret;
    }

    public static String[] readLines(File file) throws IOException
    {
        FileInputStream fileInputStream = new FileInputStream(file);
        //byte[] input = new byte[fileInputStream.available()];
        //fileInputStream.read(input);
        byte[] input = fileInputStream.readAllBytes();
        fileInputStream.close();
        String inputStr = new String(input, StandardCharsets.UTF_8);
        return StringUtils.splitByLines(inputStr);
    }

    public static String[] readLines(URL file) throws IOException
    {
        InputStream is = file.openStream();
        //byte[] input = new byte[fileInputStream.available()];
        //fileInputStream.read(input);
        byte[] input = is.readAllBytes();
        Log.warning("todo remove it. readLines byte size: "+input.length);
        is.close();
        String inputStr = new String(input, StandardCharsets.UTF_8);
        Log.warning("read string: "+inputStr);
        return StringUtils.splitByLines(inputStr);
    }

    public static void writeToURL(URL url, byte[] bytes) throws IOException
    {
        String urlString = url.toString();

        String protocol = url.getProtocol();
        var writer = protocolWriters.get(protocol);

        if (writer == null)
            throw new IOException("Wrong protocol! " + url);
        else
            writer.write(url, bytes);
    }

    public static Element toXml(String text) throws Exception
    {
        return xmlReader.build(new StringReader(text)).getRootElement();
    }

    public static String xmlToString(Element element)
    {
        return xmlWriter.outputString(element);
    }

    public static void addWriter(String protocol, UrlByteWriter writer)
    {
        protocolWriters.put(protocol, writer);
    }
}
