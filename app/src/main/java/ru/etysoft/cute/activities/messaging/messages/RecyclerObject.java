package ru.etysoft.cute.activities.messaging.messages;

import android.widget.ListView;

import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerObject {

    private RecyclerView parent;

    public RecyclerObject(RecyclerView parent)
    {
        this.parent = parent;
    }

    public RecyclerView getParent() {
        return parent;
    }
}
