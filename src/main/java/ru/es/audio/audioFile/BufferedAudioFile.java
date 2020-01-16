package ru.es.audio.audioFile;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.util.ListUtils;
import javafx.beans.property.SimpleDoubleProperty;
import ru.es.lang.ESEventDispatcher;
import ru.es.jfx.binding.ESProperty;
import ru.es.thread.ESThreadPoolManager;
import ru.es.thread.RunnableImpl;
import ru.es.util.TimeUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by saniller on 11.04.2017.
 */
public class BufferedAudioFile extends RecordableAudioBuffer
{
    public static IAudioSampleUtils utils;

    public File file;

    public ESProperty<String> info = new ESProperty<>("");
    public ESProperty<String> timeInfo = new ESProperty<>("00:00:000");

    public ESProperty<Integer> fileSampleLength = new ESProperty<>(0);
    public SimpleDoubleProperty fileSampleRate = new SimpleDoubleProperty(44100.0);

    public ESEventDispatcher dataChanged = new ESEventDispatcher();

    // load file
    public BufferedAudioFile(IAudioFileManager afm, File f, IBufferedAudioFileUser user, boolean isLoad)
    {
        file = f;

        if (isLoad)
            loadAudioFile(afm, f, user);

        updateSampleTime();
    }


    public boolean loadAudioFile(IAudioFileManager afm, File f, IBufferedAudioFileUser user)
    {
        SampleData result = utils.loadAudioFile(user.getDialogManager(), f);

        if (result == null && user != null)
        {
            afm.getLostFiles().put(this, f);
            if (afm.getLostAudioFileUsers().containsKey(this))
                afm.getLostAudioFileUsers().get(this).add(user);
            else
                afm.getLostAudioFileUsers().put(this, ListUtils.createList(user));

            return false;
        }

        if (result.data2.length != 0)
        {
            allDataCompiled2 = result.data2;
            fileSampleRate.set(result.sampleRate.doubleValue());
            //fileSampleLength.set(result.data[0].length);
            fileSampleLength.set(result.data2[0].limit() / 4); //float = *4
            updateSampleTime();
            dataChanged.run();
            
        }

        file = f;
        info.set(file.getName());

        return true;
    }

    public void recordNext(float[][] inputs)
    {
        if (inputs.length == 0)
            return;

        int stereo = inputs.length;
        int bufferSize = inputs[0].length;

        ByteBuffer[] rec = new ByteBuffer[stereo];
        for (int st = 0; st < rec.length; st++)
        {
            rec[st] = ByteBuffer.allocateDirect(bufferSize * 4);

            for (int i = 0; i < bufferSize; i++)
            {
                rec[st].putFloat(inputs[st][i]);
            }
        }

        tmpRecordedFloats.add(rec);
    }



    RunnableImpl updateTimeInfo = new RunnableImpl()
    {
        @Override
        public void runImpl() throws Exception
        {
            if (!tmpRecordedFloats.isEmpty())
            {                                                                              // is buffer size
                long time = utils.convertBuffersToTime(tmpRecordedFloats.size(), tmpRecordedFloats.get(0)[0].limit(), (float) fileSampleRate.get());
                timeInfo.set(TimeUtils.getMinutesSecondsMillis(time));
            }
            else
            {
                if (allDataCompiled2 == null || allDataCompiled2.length == 0)
                    timeInfo.set("00:00:000");
                else
                {
                    long time = utils.convertBuffersToTime(allDataCompiled2[0].limit()/4, (float) fileSampleRate.get());
                    timeInfo.set(TimeUtils.getMinutesSecondsMillis(time));
                }
            }
        }
    };

    public void updateSampleTime()
    {
        ESThreadPoolManager.getInstance().addGUITask(this, updateTimeInfo);
    }

    public void saveRecordedSampleNow() throws IOException
    {
        utils.saveToFile(allDataCompiled2, (float) fileSampleRate.get(), 32, file);
        ESThreadPoolManager.getInstance().runLater(()->info.set(file.getName()));
    }


    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void cacheGraphics()
    {
        super.cacheGraphics();
        dataChanged.event();
    }

}
