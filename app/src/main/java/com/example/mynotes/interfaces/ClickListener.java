package com.example.mynotes.interfaces;

import android.view.View;

import com.example.mynotes.models.NoteModel;

public interface ClickListener {
    void onItemClick(View view, NoteModel noteModel);
}
