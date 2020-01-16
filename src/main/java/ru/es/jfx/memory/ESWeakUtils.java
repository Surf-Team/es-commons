package ru.es.jfx.memory;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Created by saniller on 19.04.2017.
 *
 * Основные проблемы утечки памяти и их решения:
 *
 * Если мы создаём любой Pane и заполняем его контентом с любыми ссылками на текущий объект,
 * то нам не будет достаточно удалить ссылки на родителя или сделать Stage.close()
 * Необходимо очистить каждый Pane всех потомков текущего Pane.
 * Для этого поможет метод removeAllNodesFromPane
 *
 * Избегать создания Pane со ссылками на объект, при том что Pane так и не добавлен в главный родитель.
 * Т.е. если Pane лежит безхозно и не добавляется ни куда, то контент Pane не считается слабым
 *
 * Полностью избевиться и не использовать обычные ChangeListener на внешние сильные Property.
 * Вместо этого наследовать ESWeakUtils (главным классом или сделать внутренний класс с переменной),
 * и затем использовать его внутренний класс InternalWeakProperty, например:
 * new InternalWeakProperty<>(ProgramSettings.getInstance().soundOnClickForPresetManagers).addListener(...)
 * Это является эквивалентом создания WeakChangeListener с указанием внутри ChangeListener, который является полем класса.
 * Однако это менее удобно.
 *
 * Другой опыт:
 * Если не понятно, почему объект не финализируется, то часто проблемы лежат с Pane и его контентом, который содержит ссылки на объект.
 * Однажды были замечены проблемы с методом somethingNode.setOnAction(handle..ссылки на объект..)
 */
public class ESWeakUtils
{
    public static void removeAllNodesFromPane(Pane p)
    {
        for (Node n : p.getChildren())
        {
            if (n instanceof Pane)
            {
                removeAllNodesFromPane((Pane) n);
            }
        }
        p.getChildren().clear();
    }
}
