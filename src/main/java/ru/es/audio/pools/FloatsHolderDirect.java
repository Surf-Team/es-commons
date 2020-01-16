package ru.es.audio.pools;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;
import ru.es.models.DirectFloatBufferWrapper;
import ru.es.models.ReusablePool;

public class FloatsHolderDirect
{
    private final ReusablePool<DirectFloatBufferWrapper> reusablePoolMono;
    private final ReusablePool<DirectFloatBufferWrapper[]> reusablePoolStereo;
    public int bufferSize = 1024;

    public FloatsHolderDirect()
    {
        reusablePoolMono = new ReusablePool<DirectFloatBufferWrapper>("FloatsDirectHolderMono", false,
                Integer.MAX_VALUE, 60) {
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            protected DirectFloatBufferWrapper createNew()
            {
                DirectFloatBufferWrapper ret = new DirectFloatBufferWrapper(bufferSize);

                for (int i = 0; i < bufferSize; i++)
                    ret.putFloat(0f);

                return ret;
            }


            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void clean(DirectFloatBufferWrapper floats)
            {
                floats.position(0);
                for (int i = 0; i < bufferSize; i++)
                    floats.putFloat(0f);
                floats.position(0);
            }
        };
        reusablePoolStereo = new ReusablePool<DirectFloatBufferWrapper[]>("FloatsDirectHolderStereo", false,
                Integer.MAX_VALUE, 60) {
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            protected DirectFloatBufferWrapper[] createNew()
            {
                DirectFloatBufferWrapper[] ret =  { new DirectFloatBufferWrapper(bufferSize),
                        new DirectFloatBufferWrapper(bufferSize) };

                //Log.warning("create new d.b with size: "+bufferSize);

                DirectFloatBufferWrapper buffer = ret[0];
                for (int i = 0; i < bufferSize; i++)
                    buffer.putFloat(0f);

                buffer = ret[1];
                for (int i = 0; i < bufferSize; i++)
                    buffer.putFloat(0f);

                return ret;
            }


            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            @Override
            public void clean(DirectFloatBufferWrapper[] floats)
            {
                DirectFloatBufferWrapper buffer = floats[0];
                buffer.position(0);
                for (int i = 0; i < bufferSize; i++)
                    buffer.putFloat(0f);
                buffer.position(0);

                buffer = floats[1];
                buffer.position(0);
                for (int i = 0; i < bufferSize; i++)
                    buffer.putFloat(0f);
                buffer.position(0);

            }
        };        
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public DirectFloatBufferWrapper getClean()
    {
        DirectFloatBufferWrapper ret =  reusablePoolMono.getClean();

        if (ret.limit() != bufferSize)
        {
            Log.debug("DirectFloatBufferWrapper: wrong BS. creating new array");
            ret = new DirectFloatBufferWrapper(bufferSize);
        }
        return ret;
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFree(DirectFloatBufferWrapper free)
    {
        if (free.limit() == bufferSize)
            reusablePoolMono.addFree(free);
        else
            Log.warning("FloatsHolderDirect: Wrong buffer size.");
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public  DirectFloatBufferWrapper[] getFreeStereo()
    {
        DirectFloatBufferWrapper[] ret = reusablePoolStereo.getClean();

        if (ret[0].limit() != bufferSize || ret[1].limit() != bufferSize)
        {
            Log.debug("DirectFloatBufferWrapper: wrong BS. creating new array");
            ret = new DirectFloatBufferWrapper[] { new DirectFloatBufferWrapper(bufferSize),
                    new DirectFloatBufferWrapper(bufferSize) };
        }

        return ret;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFreeStereo(DirectFloatBufferWrapper[] free)
    {
        if (free == null)
            return;

        if (free[0].limit() == bufferSize)
            reusablePoolStereo.addFree(free);
        else
            Log.warning("FloatsHolderDirect (stereo): Wrong buffer size.");         
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void clear()
    {
        reusablePoolMono.clean.clear();
        reusablePoolMono.dirty.clear();
        reusablePoolStereo.clean.clear();
        reusablePoolStereo.dirty.clear();
    }
}
