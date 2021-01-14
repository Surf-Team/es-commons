Базовая библиотека с часто-используемыми функциями для всех проектов.

Одна из зависимостей отсутствует в maven:
mvn install:install-file -Dfile=.\lib\allatori-annotations.jar -DgroupId=com.allatori -DartifactId=annotations -Dversion=6.9 -Dpackaging=jar -DgeneratePom=true