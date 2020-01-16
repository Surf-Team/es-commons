package ru.es.audio.audioFile;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;
import ru.es.models.ByteFloatBuffer;
import ru.es.models.FloatArrayBuffer;
import ru.es.models.FloatBuffer;
import javolution.util.FastMap;
import javolution.util.FastTable;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * Created by saniller on 24.04.2017.
 */
public class RecordableAudioBuffer
{
    public List<ByteBuffer[]> tmpRecordedFloats = new FastTable<>();
    public FloatBuffer[] allDataCompiled2;

    private GraphicsCache graphicsCache = null;

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public static class GraphicsCache
    {
        public int imageQs[];
        // q, array
        public Map<Integer, FloatBuffer> imagesLMin = new FastMap<>();
        public Map<Integer, FloatBuffer> imagesRMin = new FastMap<>();
        public Map<Integer, FloatBuffer> imagesLMax = new FastMap<>();
        public Map<Integer, FloatBuffer> imagesRMax = new FastMap<>();
    }

    public GraphicsCache getGraphicsCache()
    {
        if (graphicsCache == null)
        {
            cacheGraphics();
        }
        return graphicsCache;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void completeRecord(int bufferSize)
    {
        if (tmpRecordedFloats.isEmpty())
            return;

        long startTime = System.nanoTime();

        int stereoSize = tmpRecordedFloats.get(0).length;
        int pos = 0;

        if (stereoSize > 1)
        {
            float[][] bb = new float[2][tmpRecordedFloats.size() * bufferSize];
            allDataCompiled2 = new FloatBuffer[]{new FloatArrayBuffer(bb[0]), new FloatArrayBuffer(bb[1]) };

            for (ByteBuffer buff[] : tmpRecordedFloats)
            {
                buff[0].position(0);
                buff[1].position(0);
                for (int i = 0; i < bufferSize; i++)
                {
                    bb[0][pos] = buff[0].getFloat();
                    bb[1][pos] = buff[1].getFloat();
                    pos++;
                }
            }
        }
        else
        {
            float[][] bb = new float[1][tmpRecordedFloats.size() * bufferSize];
            allDataCompiled2 = new FloatBuffer[]{new FloatArrayBuffer(bb[0])};

            for (ByteBuffer buff[] : tmpRecordedFloats)
            {
                buff[0].position(0);
                for (int i = 0; i < bufferSize; i++)
                {
                    bb[0][pos] = buff[0].getFloat();
                    pos++;
                }
            }
        }

        Log.warning("completeRecord copy (micros): "+((System.nanoTime() - startTime)/1000));

        tmpRecordedFloats.clear();
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void cacheGraphics()
    {
        Log.warning("Cache graphics for: "+toString());
        graphicsCache = new GraphicsCache();

        synchronized (allDataCompiled2)
        {
            long startTime = System.nanoTime();

            int length = allDataCompiled2[0].limit() / 4;

            Map<Integer, FloatBuffer> imagesLMin = graphicsCache.imagesLMin;
            Map<Integer, FloatBuffer> imagesRMin = graphicsCache.imagesRMin;
            Map<Integer, FloatBuffer> imagesLMax = graphicsCache.imagesLMax;
            Map<Integer, FloatBuffer> imagesRMax = graphicsCache.imagesRMax;

            // stable: 15, 4 = 4,8,16... 65536
            int qStages = 13;
            int startQ = 6;
            graphicsCache.imageQs = new int[qStages];
            int[] imageQs = graphicsCache.imageQs;

            for (int i = 0; i < qStages; i++)
            {
                imageQs[i] = (int) Math.pow(2, i + startQ-2);
                Log.warning("q: "+imageQs[i]);

                imagesLMin.put(imageQs[i], new ByteFloatBuffer(ByteBuffer.allocateDirect((length / imageQs[i] + 1) * 4)));
                imagesRMin.put(imageQs[i], new ByteFloatBuffer(ByteBuffer.allocateDirect((length / imageQs[i] + 1) * 4)));
                imagesLMax.put(imageQs[i], new ByteFloatBuffer(ByteBuffer.allocateDirect((length / imageQs[i] + 1) * 4)));
                imagesRMax.put(imageQs[i], new ByteFloatBuffer(ByteBuffer.allocateDirect((length / imageQs[i] + 1) * 4)));
            }

            {
                FloatBuffer lastLeftMin = allDataCompiled2[0];
                FloatBuffer lastLeftMax = allDataCompiled2[0];


                FloatBuffer curLeftMin;
                FloatBuffer curLeftMax;
                float leftMin;
                float leftMax;
                int lastEach = 1;
                int multedEach = 0;

                for (int p = 0; p < imageQs.length; p++)
                {
                    Thread.yield();
                    int each = imageQs[p];
                    curLeftMin = imagesLMin.get(each);
                    curLeftMax = imagesLMax.get(each);
                    curLeftMin.position(0);
                    curLeftMax.position(0);
                    lastLeftMax.position(0);
                    lastLeftMin.position(0);

                    int cycles = 0;
                    leftMin = 999;
                    leftMax = -999;

                    multedEach = each / lastEach;
                    lastEach = each;
                    float leftMinTmp;
                    float leftMaxTmp;

                    for (int i = 0; i < lastLeftMax.limit() / 4; i++)
                    {
                        if (lastLeftMin != lastLeftMax)
                        {
                            leftMinTmp = lastLeftMin.getFloat();
                            leftMaxTmp = lastLeftMax.getFloat();
                        }
                        else
                        {
                            leftMinTmp = lastLeftMin.getFloat();
                            leftMaxTmp = leftMinTmp;
                        }

                        if (leftMaxTmp > leftMax)
                            leftMax = leftMaxTmp;
                        if (leftMinTmp < leftMin)
                            leftMin = leftMinTmp;

                        if (i % multedEach == 0)
                        {
                            curLeftMax.putFloat(leftMax);
                            curLeftMin.putFloat(leftMin);
                            leftMin = 999;
                            leftMax = -999;

                            cycles++;
                        }
                    }
                    cycles++;

                    if (curLeftMax.limit() / 4 > cycles)
                    {
                        if (leftMin == 999)
                            leftMin = 0;
                        if (leftMax == -999)
                            leftMax = 0;

                        curLeftMax.putFloat(leftMax);
                        curLeftMin.putFloat(leftMin);
                    }

                    lastLeftMax = curLeftMax;
                    lastLeftMax.position(0);
                    lastLeftMin = curLeftMin;
                    lastLeftMin.position(0);
                }
            }


            if (allDataCompiled2.length > 1)
            {
                FloatBuffer lastRightMin = allDataCompiled2[1];
                FloatBuffer lastRightMax = allDataCompiled2[1];
                lastRightMin.position(0);
                lastRightMax.position(0);
                FloatBuffer curRightMax;
                FloatBuffer curRightMin;
                float rightMin;
                float rightMax;

                int lastEach = 1;
                int multedEach = 0;

                float leftMinTmp;
                float leftMaxTmp;

                for (int p = 0; p < imageQs.length; p++)
                {
                    int each = imageQs[p];
                    curRightMin = imagesRMin.get(imageQs[p]);
                    curRightMax = imagesRMax.get(imageQs[p]);
                    curRightMin.position(0);
                    curRightMax.position(0);

                    int cycles = 0;
                    rightMin = 999;
                    rightMax = -999;

                    multedEach = each / lastEach;
                    lastEach = each;

                    for (int i = 0; i < lastRightMin.limit() / 4; i++)
                    {
                        //lastRightMin.position(i * 4);
                        //leftMinTmp = lastRightMin.getFloat();
                        //lastRightMax.position(i * 4);
                        //leftMaxTmp = lastRightMax.getFloat();
                        if (lastRightMin != lastRightMax)
                        {
                            leftMinTmp = lastRightMin.getFloat();
                            leftMaxTmp = lastRightMax.getFloat();
                        }
                        else
                        {
                            leftMinTmp = lastRightMin.getFloat();
                            leftMaxTmp = leftMinTmp;
                        }

                        if (leftMaxTmp > rightMax)
                            rightMax = leftMaxTmp;
                        if (leftMinTmp < rightMin)
                            rightMin = leftMinTmp;

                        if (i % multedEach == 0)
                        {
                            curRightMax.putFloat(rightMax);
                            curRightMin.putFloat(rightMin);
                            //leftMaxTmp = rightMax;
                            //leftMinTmp = rightMin;
                            rightMin = 999;
                            rightMax = -999;

                            cycles++;
                        }
                    }
                    cycles++;

                    if (curRightMax.limit() / 4 > cycles)
                    {
                        if (rightMin == 999)
                            rightMin = 0;
                        if (rightMax == -999)
                            rightMax = 0;

                        curRightMax.putFloat(rightMax);
                        curRightMin.putFloat(rightMin);
                    }

                    lastRightMax = curRightMax;
                    lastRightMax.position(0);
                    lastRightMin = curRightMin;
                    lastRightMin.position(0);
                }
            }

            Log.warning("Audio wave cacheGraphics (micros): " + ((System.nanoTime() - startTime) / 1000));
        }
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public ByteBuffer[] compileByteBuffer(List<ByteBuffer[]> stereo)
    {
        if (stereo == null || stereo.size() == 0)
            return null;

        int bufferSize = stereo.get(0)[0].limit() /4;
        int samplesCount = stereo.size() * bufferSize;
        int stereoSize = stereo.get(0).length; //todo

        ByteBuffer[] ret = new ByteBuffer[stereoSize];

        for (int st = 0; st < stereoSize; st++)
        {
            ret[st] = ByteBuffer.allocate(samplesCount*4);
        }

        for (int st = 0;  st < stereoSize; st++)
        {
            ByteBuffer bb = ret[st];
            bb.position(0);

            for (ByteBuffer[] list : stereo)
            {
                ByteBuffer array = list[st];
                array.position(0);
                for (int i = 0; i < bufferSize; i++)
                {
                    bb.putFloat(array.getFloat());
                }
            }
        }

        return ret;
    }

}
