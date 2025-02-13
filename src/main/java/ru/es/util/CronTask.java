package ru.es.util;

import ru.es.fileCache.table.Row;

public class CronTask implements CachedRow
{
	public String periods;
	public String command;
	public String arg;
	public boolean disabled = false;

	public CronTask(String periods, String command, String arg)
	{
		this.periods = periods;
		this.command = command;
		this.arg = arg;
	}

	public CronTask(String periods, String command)
	{
		this.periods = periods;
		this.command = command;
	}

	public CronTask()
	{

	}

	@Override
	public void parse(Row row)
	{
		periods = row.getValue("periods");
		command = row.getValue("cmd");
		arg = row.getValue("arg");
		disabled = row.getValueBoolean("disabled");
	}

	@Override
	public String toString()
	{
		return command + " "+arg+" ["+periods+"]";
	}
}
