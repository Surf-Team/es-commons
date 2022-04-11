package ru.es.lang.patterns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ViewListUpdater<Key, Value>
{
    public Map<Key, Value> activeViews = new HashMap<>();
    Collection<Key> collection;

    public ViewListUpdater(Collection<Key> collection)
    {
        this.collection = collection;
    }

    public void updateCheckers()
    {
        for (Key key : collection)
        {
            if (!activeViews.containsKey(key))
            {
                activeViews.put(key, add(key));
            }
        }
        for (Key addr : new ArrayList<>(activeViews.keySet()))
        {
            if (!collection.contains(addr))
            {
                removed(activeViews.get(addr));
                activeViews.remove(addr);
            }
        }
    }

    public abstract void removed(Value value);

    public abstract Value add(Key key);

    public boolean changeChecker(Key oldKey, Key changedKey)
    {
        if (!oldKey.equals(changedKey))
        {
            removed(activeViews.get(oldKey));
            activeViews.remove(oldKey);
            activeViews.put(changedKey, add(changedKey));
            collection.remove(oldKey);
            collection.add(changedKey);
            return true;
        }
        return false;
    }
}
