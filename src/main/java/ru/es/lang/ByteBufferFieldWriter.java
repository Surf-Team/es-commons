package ru.es.lang;

import java.nio.ByteBuffer;

public interface ByteBufferFieldWriter<T>
{
	void write(T object, ByteBuffer byteBuffer);
}
