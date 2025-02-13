module ru.es.commons {
    requires transitive json.simple;
    requires transitive org.apache.commons.lang3;
    requires transitive jdom2;
    requires transitive jdk.httpserver;
    requires transitive com.google.gson;
    requires transitive javolution.core.java;
    requires java.compiler;
    requires reflections;
	requires org.apache.httpcomponents.httpclient;
    requires org.apache.httpcomponents.httpcore;
    requires org.apache.httpcomponents.httpmime;
	requires org.yaml.snakeyaml;
	requires com.fasterxml.jackson.dataformat.xml;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.databind;
	requires simpleclient;
	requires java.desktop;

	exports ru.es.annotation;
    exports ru.es.json;
    exports ru.es.lang;
    exports ru.es.fileCache.table;
    exports ru.es.lang.logic;
    exports ru.es.lang.limiters;
	exports ru.es.log;
    exports ru.es.math;
    exports ru.es.net;
    exports ru.es.reflection;
	exports ru.es.thread;
    exports ru.es.util;
	exports ru.es.fileCache;
    exports ru.es.exceptions;
	exports ru.es.util.writers;
    exports ru.es.yaml;
	exports ru.es.process;
	exports ru.es.xml;
	exports ru.es.prometheus;
}