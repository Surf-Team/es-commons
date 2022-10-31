package ru.es.models;

import com.fasterxml.jackson.dataformat.xml.annotation.*;

public class VersionInfo
{
	@JacksonXmlProperty(isAttribute = true)
	public int libVersion = 0;

	public long buildTimeStamp;
	public String buildDate;
	public int coreVersion = 0;
}
