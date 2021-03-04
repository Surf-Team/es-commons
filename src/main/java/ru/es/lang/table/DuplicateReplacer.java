package ru.es.lang.table;

public interface DuplicateReplacer
{
    String rename(String duplicatedValue, Row p, int lineIndex, int existIndex);
}
