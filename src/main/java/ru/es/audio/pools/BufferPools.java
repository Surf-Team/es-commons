package ru.es.audio.pools;


import ru.es.jfx.binding.ESProperty;

public class BufferPools
{
    public final ESProperty<Integer> bufferSize = new ESProperty<>(1024);

    public final FloatsHolder floatsHolder = new FloatsHolder();
    public final BooleanHolder booleanHolder = new BooleanHolder();
    public final DoublesHolder doublesHolder = new DoublesHolder();
    public final FloatsHolderDirect floatsHolderDirect = new FloatsHolderDirect();

    public BufferPools()
    {
        bufferSize.addListener(o->bufferSizeChanged());
        bufferSizeChanged();
    }

    public void bufferSizeChanged()
    {
        floatsHolder.bufferSize = bufferSize.get();
        floatsHolder.clear();

        booleanHolder.bufferSize = bufferSize.get();
        booleanHolder.clear();

        doublesHolder.bufferSize = bufferSize.get();
        doublesHolder.clear();

        floatsHolderDirect.bufferSize = bufferSize.get();
        floatsHolderDirect.clear();
    }

}
