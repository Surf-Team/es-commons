package ru.es.audio;

import com.allatori.annotations.ControlFlowObfuscation;
import ru.es.audio.pools.BufferPools;

public class AudioStamp
{
    public final long position;
    public final int bufferSize;
    public final float sampleRate;
    public final float bpm;
    public final boolean soundStarted;
    public final long arrangeTick;
    public final int arrangeJumpIndex;
    public final BufferPools pools;

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public AudioStamp(final long position, final int bufferSize, final float sampleRate, final float bpm, final boolean soundStarted,
                      final long arrangeTick, int arrangeJumpIndex, BufferPools bufferPools) {
        this.bufferSize = bufferSize;
        this.sampleRate = sampleRate;
        this.bpm = bpm;
        this.soundStarted = soundStarted;
        this.position = position;
        this.arrangeTick = arrangeTick;
        this.arrangeJumpIndex = arrangeJumpIndex;
        this.pools = bufferPools;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public int getBufferSize()
    {
        return bufferSize;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float getSampleRate()
    {
        return sampleRate;
    }

    @ControlFlowObfuscation(ControlFlowObfuscation.DISABLE)
    public float getBpm()
    {
        return bpm;
    }
}
