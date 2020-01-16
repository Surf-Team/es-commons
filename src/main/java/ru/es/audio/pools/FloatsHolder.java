package ru.es.audio.pools;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;
import ru.es.models.ReusablePool;

public class FloatsHolder
{
    public static boolean holderOn = true;

    private final ReusablePool<float[]> reusablePoolMono;
    private final ReusablePool<float[][]> reusablePoolStereo;
    public int bufferSize = 1024;

    public FloatsHolder()
    {
        reusablePoolMono = new ReusablePool<float[]>("FloatsHolderMono", false, 20000, 10) {
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            protected float[] createNew()
            {
                return new float[bufferSize];
            }


            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void clean(float[] floats)
            {
                for (int i = 0; i < floats.length; i++)
                    floats[i] = 0f;
            }
        };
        reusablePoolMono.doLog = true;
        reusablePoolStereo = new ReusablePool<float[][]>("FloatsHolderStereo", false, 20000, 10) {
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            protected float[][] createNew()
            {
                return new float[2][bufferSize];
            }


            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void clean(float[][] floats)
            {
                float[] arr = floats[0];
                for (int i = 0; i < arr.length; i++)
                    arr[i] = 0f;
                arr = floats[1];
                for (int i = 0; i < arr.length; i++)
                    arr[i] = 0f;
            }
        };
        reusablePoolStereo.doLog = true;        
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float[] getClean()
    {
        if (!holderOn)
            return new float[bufferSize];

        float[] ret = reusablePoolMono.getClean();
        if (ret.length != bufferSize)
        {
            Log.debug("FloatsHolder: wrong BS. creating new array");
            return new float[bufferSize];
        }

        return ret;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float[][] getFreeStereo()
    {
        if (!holderOn)
            return new float[2][bufferSize];

        float[][] ret = reusablePoolStereo.getClean();

        /*for (float ff[] : ret)
            for (float f : ff)
                if (f != 0)
                {
                    Log.warning("Error in stereo floats holder! clean is not clean!");
                    break;
                }         */

        if (ret[0].length != bufferSize || ret[1].length != bufferSize)
        {
            Log.debug("FloatsHolder: wrong BS (stereo). creating new array");
            return new float[2][bufferSize];
        }

        return ret;
    }

    public float[][] checkStereo(float[][] f)
    {
        if (f == null)
        {
            Log.warning("Creating free stereo");
            return getFreeStereo();
        }
        else if (f[0].length != bufferSize)
        {
            Log.warning("Creating free stereo");
            return getFreeStereo();
        }
        return f;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFreeStereo(float[][] free, int process)
    {
        //if (process == 3)
        //return;


        //if (process > 0)
        //return;

        // process используются: 0 - 10
        if (!holderOn)
            return;

        if (free == null)
            return;

        reusablePoolStereo.addFree(free);
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFree(float[] free, int process)
    {
        // 3 - ерундит
        //if (process == 3)
//                return;

        //  process используются: 0 -> 23
        if (!holderOn)
            return;
        reusablePoolMono.addFree(free);
    }

    public void clear()
    {
        reusablePoolMono.clean.clear();
        reusablePoolMono.dirty.clear();
        reusablePoolStereo.clean.clear();
        reusablePoolStereo.dirty.clear();
    }
}
