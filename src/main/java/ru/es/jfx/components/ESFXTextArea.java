package ru.es.jfx.components;

import ru.es.jfx.binding.ESProperty;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TextArea;

public class ESFXTextArea extends TextArea
{
    public ESFXTextArea()
    {

    }

    public ESProperty<Boolean> autoScroll = new ESProperty<>(true);
    public ESProperty<Boolean> hasLimit = new ESProperty<>(true);
    public ESProperty<Integer> limitText = new ESProperty<Integer>(50000);
    public ESProperty<Double> limitBurst = new ESProperty<Double>(1.2);

    @Override
    public void appendText(String text)
    {
        IndexRange selection = getSelection();

        if (autoScroll.get())
        {
            super.appendText(text);
            if (hasLimit.get())
            {
                int maxLimit = (int) (limitText.get() * limitBurst.get());
                if (getText().length() > maxLimit)
                    super.setText(text.substring(text.length() - limitText.get()));
            }
        }
        else
        {
            if (hasLimit.get())
            {
                text = getText() + text;
                int maxLimit = (int) (limitText.get() * limitBurst.get());
                if (text.length() > maxLimit)
                    text = text.substring(text.length() - limitText.get());

                double scroll = getScrollTop();
                super.setText(text);
                setScrollTop(scroll);
            }
            else
            {
                double scroll = getScrollTop();
                super.setText(getText()+text);
                setScrollTop(scroll);
            }
        }

        if (!autoScroll.get())
            selectRange(selection.getStart(), selection.getEnd());
    }
}
