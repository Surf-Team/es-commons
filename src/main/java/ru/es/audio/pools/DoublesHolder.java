package ru.es.audio.pools;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.log.Log;
import ru.es.models.ReusablePool;

public class DoublesHolder
{
    public boolean enabled = true;
    public int bufferSize = 1024;

    private ReusablePool<double[]> reusablePoolMono;

    public DoublesHolder()
    {
        reusablePoolMono = new ReusablePool<double[]>("DoubleHolderMono", false, 20000, 10)
        {
            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            protected double[] createNew()
            {
                return new double[bufferSize];
            }


            @Override
            @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
            public void clean(double[] array)
            {
                //for (int i = 0; i < array.length; i++)
                //array[i] = 0.0;
            }
        };
        reusablePoolMono.doLog = true;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public double[] getClean()
    {
        if (!enabled)
            return new double[bufferSize];

        double[] ret = reusablePoolMono.getClean();

        if (ret.length != bufferSize)
        {
            ret = new double[bufferSize];
            Log.debug("DoublesHolder: wrong bs. Create new.");
        }

        return ret;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void addFree(double[] free, int process)
    {
        if (!enabled)
            return;

        reusablePoolMono.addFree(free);
    }

    public void clear()
    {
        reusablePoolMono.clean.clear();
        reusablePoolMono.dirty.clear();
    }
}
