package ru.es.audio.pools;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;
import ru.es.models.ReusablePool;

public class BooleanHolder
{
    private final ReusablePool<boolean[]> reusablePool;
    public int bufferSize = 1024;

    public BooleanHolder()
    {
        reusablePool = new ReusablePool<boolean[]>("BooleanHolder", false, Integer.MAX_VALUE, 60) {
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            protected boolean[] createNew()
            {
                return new boolean[bufferSize];
            }


            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void clean(boolean[] floats)
            {

            }
        };
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean[] getClean()
    {
        boolean[] ret = reusablePool.getClean();

        if (ret.length != bufferSize)
        {
            ret = new boolean[bufferSize];
            Log.debug("BooleanHolder: wrong bs. Create new.");
        }

        return ret;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFree(boolean[] free)
    {
        reusablePool.addFree(free);
    }


    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public boolean[] getDirty()
    {
        return getClean();
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void clear()
    {
        reusablePool.dirty.clear();
        reusablePool.clean.clear();
    }
}
