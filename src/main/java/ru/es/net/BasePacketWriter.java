package ru.es.net;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class BasePacketWriter
{
	public ByteBuffer byteBuffer;

	public BasePacketWriter(ByteBuffer byteBuffer)
	{
		this.byteBuffer = byteBuffer;
	}

	public BasePacketWriter()
	{
	}

	protected ByteBuffer getByteBuffer()
	{
		return byteBuffer;
	}

	public void writeShort(int value)
	{
		getByteBuffer().putShort((short) value);
	}

	public void writeInt(int value)
	{
		getByteBuffer().putInt(value);
	}


	public void writeByte(int data)
	{
		getByteBuffer().put((byte) data);
	}


	public void writeFloat(double value)
	{
		getByteBuffer().putFloat((float) value);

	}

	public void writeLong(long value)
	{
		getByteBuffer().putLong(value);
	}

	public void writeByteArray(byte[] data)
	{
		writeByte(data.length);
		getByteBuffer().put(data);
	}

	public void writeString(CharSequence charSequence)
	{
		if (charSequence != null)
			addByteArray(stringToBytesUtf8(charSequence.toString()));
		else
			addByteArray(stringToBytesUtf8(""));
	}


	public static byte[] stringToBytesUtf8(String string)
	{
		if (string == null)
		{
			return new byte[0];
		}
		try
		{
			return string.getBytes(StandardCharsets.UTF_8);
		}
		catch (Exception e) {
		}
		return new byte[0];
	}


	public void addByteArray(byte[] value)
	{
		if (value == null)
		{
			writeByte(0);
			return;
		}

		writeInt(value.length);
		getByteBuffer().put(value);
	}
}
