package ru.es.scripts;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ru.es.fileCache.table.TSVTable;
import ru.es.log.Log;
import ru.es.util.FileUtils;
import ru.es.util.JSONUtils;

import java.io.File;
import java.io.IOException;

public class UEMusicParser
{
	public static void main(String[] args) throws IOException
	{
		TSVTable table = new TSVTable(new File("C:/Users/sanil/Desktop/music.tsv"), "id");

		JsonObject root = new JsonObject();
		JsonArray array = new JsonArray();
		root.add("rawArray", array);

		Log.warning("rows: "+table.rows.size());
		for (var e : table.rows)
		{
			JsonObject jsonObject = new JsonObject();
			array.add(jsonObject);

			jsonObject.addProperty("id", e.id);

			int cnt = e.getValueInt("cnt");

			JsonArray sounds = new JsonArray();
			jsonObject.add("sounds", sounds);
			for (int i = 0; i < cnt; i++)
			{
				sounds.add(e.getValue("str["+i+"]"));
			}
		}

		String pretty = JSONUtils.prettyGson.toJson(root);
		FileUtils.writeFile(new File("C:/Users/sanil/Desktop/music.json"), pretty);
	}
}
