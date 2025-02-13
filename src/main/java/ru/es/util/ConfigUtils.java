package ru.es.util;

import ru.es.log.Log;
import ru.es.exceptions.PropFileNotFoundException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

public class ConfigUtils
{
	// subFolder - подпапка из директории запуска
	// configName - то что идёт до точки
	// stageNames - то что идёт после точки (стейджи перезаписываются друг за другом).
	// Сначала читается defaults, затем по умолчанию developer. Если укажем production, то прочитается defaults, затем production
	@Deprecated //todo испольщуем loadProperties без указания subFolder
	public static Properties loadProperties(String subFolder, String configName, String... stageNames) throws IOException
	{
		return loadPropertiesInPatch(".", subFolder, configName, stageNames);
	}

	public static ESProperties loadProperties(String configName, String... stageNames) throws IOException
	{
		return loadPropertiesInPatch(".", "", configName, stageNames);
	}

	public static ESProperties loadProperties(String configName, boolean allowEnvVariables, String... stageNames) throws IOException
	{
		ESProperties ret = null;
		try
		{
			 ret = loadPropertiesInPatch(".", "", configName, stageNames);
		}
		catch (IOException exception)
		{
			if (allowEnvVariables && exception instanceof PropFileNotFoundException)
			{
				Log.warning("Config file not found. Getting properties from ENV variables?");
				ret = ((PropFileNotFoundException) exception).defaultProps;
			}
			else
				throw exception;
		}

		if (allowEnvVariables)
			ConfigUtils.loadFromSystemEnv(ret, false);

		return ret;
	}

	public static void loadFromSystemEnv(Properties config, boolean debug)
	{
		for (var v : System.getenv().entrySet())
		{
			config.setProperty(v.getKey(), v.getValue());
			if (debug)
			{
				Log.warning("Set property from system env variable: "+v.getKey()+"="+v.getValue());
			}
		}
	}

	// вариант, когда нужно указать начальную директорию
	public static ESProperties loadPropertiesInPatch(String initPatch, String subFolder, String configName, String... stageNames) throws IOException
	{
		// изначально все настройки считываются из config.defaults
		File configFile = new File(initPatch+"/"+subFolder+"/"+configName+".defaults.properties");
		ESProperties config = FileUtils.getPropertiesFile(configFile);

		// затем в эти же properties перезаписываются значения из окружения (developer, preproduction, production)
		// окруженние указывается в первом аргументе запуска. Если не указано, то будет developer
		if (stageNames.length == 0)
			stageNames = new String[] {"developer"};
		// может быть указано сразу несколько окружений для перезаписи
		for (String stage : stageNames)
		{
			try
			{
				File stageConfigFile = new File(initPatch + "/" + subFolder + "/" + configName + "." + stage + ".properties");
				Log.warning("Loading config: " + stageConfigFile.getName());
				Properties stageConfig = FileUtils.getPropertiesFile(stageConfigFile);
				// перезаписыванием значения базового конфига
				config.putAll(stageConfig);
			}
			catch (IOException e)
			{
				throw new PropFileNotFoundException(config);
			}
		}

		return config;
	}

	// вариант, когда нужно указать начальную директорию
	public static Properties loadPropertiesInPatch(URL initPatch, String subFolder, String configName, String... stageNames) throws IOException
	{
		// изначально все настройки считываются из config.defaults
		URL configFile = new URL(initPatch+"/"+subFolder+"/"+configName+".defaults.properties");
		Properties config = FileUtils.loadProperties(configFile);

		// затем в эти же properties перезаписываются значения из окружения (developer, preproduction, production)
		// окруженние указывается в первом аргументе запуска. Если не указано, то будет developer
		if (stageNames.length == 0)
			stageNames = new String[] {"developer"};
		// может быть указано сразу несколько окружений для перезаписи
		for (String stage : stageNames)
		{
			URL stageConfigFile = new URL(initPatch+"/"+subFolder+"/"+configName+"." + stage + ".properties");
			Log.warning("Loading config: "+stageConfigFile.toString());
			Properties stageConfig = FileUtils.loadProperties(stageConfigFile);
			// перезаписыванием значения базового конфига
			config.putAll(stageConfig);
		}

		return config;
	}


}
