Базовая библиотека с часто-используемыми функциями и общими классами, использующимися в других модулях.

# Краткий список полезных классов:
Limiter (count limiter, time limiter, count time limiter) - утил-классы для ограничения вызова функций
TSVTable - класс для работы с таблицами, в которых данные разделены через tab
ESMath - математические операции
Rnd - действия со случайностями
SurfFramework - фреймворк, использующийся при работе в перезагружаемых на ходу приложениях (например, Scripts поверх GameServer)
SimpleThreadPool - простой менеджер потоков
SingletonThreadPool - общий менеджер потоков
ConfigUtils, ESProperties - работа с конфигами
FileUtils - работа с файлами
HtmlUtils - работа с html
JSONUtils - работа с простыми json
ListUtils - работа со списками
MapUtils - работа с мапами
ProcessFactory - работа с процессами операционной системы
SortUtils - частые случаи сортировки
StringUtils - работа со строками
TimeUtils - работа со временем
XmlConfig - функциональный xml конфиг с поддержкой условий
YamlElement - работа с Yaml файлами
Log - простой вывод в лог
FileTemplateManager - функционал работы с шаблонами (например html шаблоны).
Prometheus.. - работа с прометеусом

# Функции для работы через аннотации с json/xml
Парсинг Json в java объекты:
DependencyManager + JsonSerializeManager + @UniqueKey

Json парсеры:
SurfJsonReader, SurfJsonWriter

XML парсеры:
AnnotatedXMLRepository, MappedXML
