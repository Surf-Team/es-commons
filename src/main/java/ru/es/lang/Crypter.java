package ru.es.lang;

public interface Crypter
{
	void crypt(byte[] bb, int start, int len);
	void decrypt(byte[] bb, int start, int len);
}
