package ru.es.scripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ru.es.log.Log;
import ru.es.util.FileUtils;
import ru.es.util.JSONUtils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.io.File;
import java.io.IOException;

public class MakePatchList
{
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException
	{
		Log.warning("arg 0 is list of file paths: ./a/b/c;./d/e/f");

		JsonArray arr = new JsonArray();
		for (String path : args[0].split(";"))
		{
			File f = new File(path);
			scan(f, arr, path);
		}
		Log.warning("pathlist: ");
		Log.warning(arr.toString());
		FileUtils.writeFile(new File("./patchlist"), JSONUtils.prettyGson.toJson(arr));
	}

	private static void scan(File folder, JsonArray arr, String path) throws IOException, NoSuchAlgorithmException
	{
		for (File f : folder.listFiles())
		{
			if (f.isDirectory())
				scan(f, arr, path+"/"+f.getName());
			else
			{
				JsonObject object = new JsonObject();
				arr.add(object);

				object.addProperty("file", path + "/" + f.getName());

				String hashString = calculateSHA256(f.getPath());
				object.addProperty("version", hashString);
			}
		}
	}

	public static String calculateSHA256(String filePath) throws IOException, NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
		byte[] hashBytes = digest.digest(fileBytes);

		// Преобразуем байты в шестнадцатеричную строку
		StringBuilder hexString = new StringBuilder();
		for (byte b : hashBytes)
		{
			String hex = Integer.toHexString(0xff & b);

			if (hex.length() == 1)
				hexString.append('0');

			hexString.append(hex);
		}
		return hexString.toString();
	}
}
