package ru.es.audio;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public enum OscType
{
    Off("Off"),
    Slave("Slave"),

    Sin("Sin"),
    Saw("Saw"),
    Square("Square"),
    Triangle("Triangle"),
    Noize("Noize"),

    RndHold("RndHold"),
    RndGlide("RndGlide"),

    SinLfo("Sin"),
    SawLfo("Saw"),
    TriangleLfo("Triangle"),
    SquareLfo("Square"),

    Wave0("Wave 0"),

    Wave3("Wave3"),Wave4("Wave4"),Wave5("Wave5"),Wave6("Wave6"),Wave7("Wave7"),Wave8("Wave8"),Wave9("Wave9"),Wave10("Wave10"),Wave11("Wave11"),Wave12("Wave12"),Wave13("Wave13"),Wave14("Wave14"),Wave15("Wave15"),Wave16("Wave16"),Wave17("Wave17"),Wave18("Wave18"),Wave19("Wave19"),Wave20("Wave20"),Wave21("Wave21"),Wave22("Wave22"),Wave23("Wave23"),Wave24("Wave24"),Wave25("Wave25"),Wave26("Wave26"),Wave27("Wave27"),Wave28("Wave28"),Wave29("Wave29"),Wave30("Wave30"),Wave31("Wave31"),Wave32("Wave32"),Wave33("Wave33"),Wave34("Wave34"),Wave35("Wave35"),Wave36("Wave36"),Wave37("Wave37"),Wave38("Wave38"),Wave39("Wave39"),Wave40("Wave40"),Wave41("Wave41"),Wave42("Wave42"),Wave43("Wave43"),Wave44("Wave44"),Wave45("Wave45"),Wave46("Wave46"),Wave47("Wave47"),Wave48("Wave48"),Wave49("Wave49"),Wave50("Wave50"),Wave51("Wave51"),Wave52("Wave52"),Wave53("Wave53"),Wave54("Wave54"),Wave55("Wave55"),Wave56("Wave56"),Wave57("Wave57"),Wave58("Wave58"),Wave59("Wave59"),Wave60("Wave60"),Wave61("Wave61"),Wave62("Wave62"),Wave63("Wave63"),Wave64("Wave64");

    public static ObservableList<OscType> osc3Types = FXCollections.observableArrayList(
            Off, Slave, Saw, Square, Sin, Triangle,
            Wave3, Wave4, Wave5, Wave6, Wave7, Wave8, Wave9, Wave10, Wave11, Wave12, Wave13, Wave14, Wave15, Wave16, Wave17, Wave18, Wave19, Wave20, Wave21, Wave22, Wave23, Wave24, Wave25, Wave26, Wave27, Wave28, Wave29, Wave30, Wave31, Wave32, Wave33, Wave34, Wave35, Wave36, Wave37, Wave38, Wave39, Wave40, Wave41, Wave42, Wave43, Wave44, Wave45, Wave46, Wave47, Wave48, Wave49, Wave50, Wave51, Wave52, Wave53, Wave54, Wave55, Wave56, Wave57, Wave58, Wave59, Wave60, Wave61, Wave62, Wave63, Wave64);
    public static ObservableList<OscType> lfoTypes = FXCollections.observableArrayList(
            SinLfo, TriangleLfo, SawLfo, SquareLfo, RndHold, RndGlide,
            Wave3, Wave4, Wave5, Wave6, Wave7, Wave8, Wave9, Wave10, Wave11, Wave12, Wave13, Wave14, Wave15, Wave16, Wave17, Wave18, Wave19, Wave20, Wave21, Wave22, Wave23, Wave24, Wave25, Wave26, Wave27, Wave28, Wave29, Wave30, Wave31, Wave32, Wave33, Wave34, Wave35, Wave36, Wave37, Wave38, Wave39, Wave40, Wave41, Wave42, Wave43, Wave44, Wave45, Wave46, Wave47, Wave48, Wave49, Wave50, Wave51, Wave52, Wave53, Wave54, Wave55, Wave56, Wave57, Wave58, Wave59, Wave60, Wave61, Wave62, Wave63, Wave64); //todo SH, SH, waves
    public static ObservableList<OscType> delayTypes = FXCollections.observableArrayList(
            SinLfo, TriangleLfo, SawLfo, SquareLfo, RndHold, RndGlide);
    public static ObservableList<OscType> synthTypes = FXCollections.observableArrayList(Sin, Triangle,
            Wave3, Wave4, Wave5, Wave6, Wave7, Wave8, Wave9, Wave10, Wave11, Wave12, Wave13, Wave14, Wave15, Wave16, Wave17, Wave18, Wave19, Wave20, Wave21, Wave22, Wave23, Wave24, Wave25, Wave26, Wave27, Wave28, Wave29, Wave30, Wave31, Wave32, Wave33, Wave34, Wave35, Wave36, Wave37, Wave38, Wave39, Wave40, Wave41, Wave42, Wave43, Wave44, Wave45, Wave46, Wave47, Wave48, Wave49, Wave50, Wave51, Wave52, Wave53, Wave54, Wave55, Wave56, Wave57, Wave58, Wave59, Wave60, Wave61, Wave62, Wave63, Wave64);

    String name;

    OscType(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
