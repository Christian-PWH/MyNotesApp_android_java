package com.example.mynotes.interfaces;

import android.view.View;

import com.example.mynotes.adapters.RecyclerViewAdapter;
import com.example.mynotes.models.NoteModel;

public interface ClickListener {
    void onItemClick(int position, View view, NoteModel noteModel);
}
