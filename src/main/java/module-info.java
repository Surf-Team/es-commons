module ru.es.commons {
    requires transitive annotations;
    requires transitive json.simple;
    requires transitive org.apache.commons.lang3;
    requires transitive jdom2;
    requires transitive jdk.httpserver;
    requires transitive com.google.gson;
    requires transitive javolution.core.java;

    exports ru.es.annotation;
    exports ru.es.json;
    exports ru.es.lang;
    exports ru.es.log;
    exports ru.es.math;
    exports ru.es.models;
    exports ru.es.net;
    exports ru.es.reflection;
    exports ru.es.thread;
    exports ru.es.util;
    exports ru.es.lang.logic;
    exports ru.es.lang.limiters;
    exports ru.es.lang.patterns;
    exports ru.es.reflection.simple;
}