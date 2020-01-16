package ru.es.jfx.application;

import ru.es.jfx.binding.ESBoolean;
import ru.es.jfx.binding.ESProperty;
import ru.es.lang.ESEventDispatcher;
import ru.es.models.ESCrypter;
import ru.es.thread.RunnableImpl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public abstract class PacksManager
{
    private Map<String, Pack> packs = new HashMap<>();
    private File packsFolder;

    public PacksManager(File packsFolder)
    {
        this.packsFolder = packsFolder;
    }


    public abstract void requestDownloadPack(Pack pack);

    public Pack getPack(String packName)
    {
        Pack pi = packs.get(packName);
        if (pi == null)
        {
            pi = new Pack(packName);
            packs.put(packName, pi);
        }

        return pi;
    }

    public Pack getPack(String packName, ESCrypter crypter)
    {
        Pack pi = packs.get(packName);
        if (pi == null)
        {
            pi = new Pack(packName);
            packs.put(packName, pi);
        }

        return pi;
    }

    public class Pack
    {
        public final String name;
        public final File folder;
        public ESBoolean downloaded = new ESBoolean(false);
        public ESEventDispatcher onDownloaded = new ESEventDispatcher();
        public File versionFile;        // version файл обязательно нужен как нимимум для проверки целостности распаковки архива
        public String downloadDesc;
        public String errorText;
        public ESProperty<String> progressInfoSetter = new ESProperty<>("Начало загрузки...");

        private Pack(String name)
        {
            this.name = name;
            this.folder = new File(packsFolder, name);
            versionFile = new File(folder, "PackInfo.xml");
            downloadDesc = "Скачивание библиотеки "+name;
            //errorText = "Polyform установлена не до конца.\nОшибка при загрузке базовой библиотеки:\n";
            errorText = "Ошибка при загрузке библиотеки "+name+":";

            if (!versionFile.exists())
            {
                downloaded.set(false);
                onDownloaded.addOnEventOnce(new RunnableImpl() {
                    @Override
                    public void runImpl() throws Exception
                    {
                        downloaded.set(true);
                    }
                });
                requestDownloadPack(this);
            }
            else
            {
                downloaded.set(true);
            }
        }

        public void ifLoad(RunnableImpl r)
        {
            if (downloaded.get())
            {
                r.run();
            }
            else
            {
                onDownloaded.addOnEventOnce(r);
            }
        }
    }


}
