package ru.es.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

public class CryptUtils
{

	public static String sha256(String base)
	{
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
			return bytesToHex(hash).toUpperCase();
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}


	public static String md5(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException
	{
		byte[] bytesOfMessage = s.getBytes(StandardCharsets.UTF_8);

		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] theMD5digest = md.digest(bytesOfMessage);

		return bytesToHex(theMD5digest);
	}


	public static String stringToSha1(String password, Charset charset)
	{
		String sha1 = "";
		try
		{
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();

			if (charset != null)
				crypt.update(password.getBytes(charset));
			else
				crypt.update(password.getBytes());

			sha1 = byteToHex(crypt.digest());
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return sha1;
	}

	public static String stringToSha256(String password, Charset charset)
	{
		String sha1 = "";
		try
		{
			MessageDigest crypt = MessageDigest.getInstance("SHA-256");
			crypt.reset();

			if (charset != null)
				crypt.update(password.getBytes(charset));
			else
				crypt.update(password.getBytes());

			sha1 = byteToHex(crypt.digest());
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return sha1;
	}


	public static String bytesToHex(byte[] bytes)
	{
		StringBuffer result = new StringBuffer();
		for (byte b : bytes) result.append(Integer.toString((b & 0xff) + 0x100,
				16).substring(1));
		return result.toString();
	}

	// возможно методы идентичные
	public static String byteToHex(final byte[] hash)
	{
		Formatter formatter = new Formatter();
		for (byte b : hash)
		{
			formatter.format("%02x", b);
		}
		String result = formatter.toString();
		formatter.close();
		return result;
	}

}
