package ru.es.util;

import ru.es.log.Log;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils
{
	// subFolder - подпапка из директории запуска
	// configName - то что идёт до точки
	// stageNames - то что идёт после точки (стейджи перезаписываются друг за другом).
	// Сначала читается defaults, затем по умолчанию developer. Если укажем production, то прочитается defaults, затем production
	public static Properties loadProperties(String subFolder, String configName, String... stageNames) throws IOException
	{
		return loadPropertiesInPatch("", subFolder, configName, stageNames);
	}

	public static Properties loadPropertiesInPatch(String initPatch, String subFolder, String configName, String... stageNames) throws IOException
	{
		// изначально все настройки считываются из config.defaults
		File configFile = new File(initPatch+"./"+subFolder+"/"+configName+".defaults.properties");
		Properties config = FileUtils.getPropertiesFile(configFile);

		// затем в эти же properties перезаписываются значения из окружения (developer, preproduction, production)
		// окруженние указывается в первом аргументе запуска. Если не указано, то будет developer
		if (stageNames.length == 0)
			stageNames = new String[] {"developer"};
		// может быть указано сразу несколько окружений для перезаписи
		for (String stage : stageNames)
		{
			File stageConfigFile = new File(initPatch+"./"+subFolder+"/"+configName+"." + stage + ".properties");
			Log.warning("Loading config: "+stageConfigFile.getName());
			Properties stageConfig = FileUtils.getPropertiesFile(stageConfigFile);
			// перезаписыванием значения базового конфига
			config.putAll(stageConfig);
		}

		return config;
	}
}
