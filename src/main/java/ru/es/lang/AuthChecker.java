package ru.es.lang;

public interface AuthChecker
{
    boolean authSuccess(String account, String password);
}
