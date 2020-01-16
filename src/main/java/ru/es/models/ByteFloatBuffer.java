package ru.es.models;

import com.allatori.annotations.ControlFlowObfuscation;

import java.nio.ByteBuffer;

public class ByteFloatBuffer implements FloatBuffer
{
    public final ByteBuffer byteBuffer;

    public ByteFloatBuffer(ByteBuffer buffer)
    {
        this.byteBuffer = buffer;
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public final int limit()
    {
        return byteBuffer.limit();
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public final void position(int pos)
    {
        byteBuffer.position(pos);
    }

    @Override
    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public final float getFloat()
    {
        return byteBuffer.getFloat();
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public void putFloat(float f)
    {
        byteBuffer.putFloat(f);
    }
}
