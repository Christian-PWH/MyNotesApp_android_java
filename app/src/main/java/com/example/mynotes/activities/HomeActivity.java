package com.example.mynotes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotes.R;
import com.example.mynotes.adapters.RecyclerViewAdapter;
import com.example.mynotes.interfaces.ClickListener;
import com.example.mynotes.models.NoteModel;
import com.example.mynotes.sqlite.DBManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private DBManager dbManager;

    private ClickListener clickListener;
    Toolbar toolbar;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    TextView infoText;

    FloatingActionButton addNote;

    ImageView itemPopUp;

    ArrayList<NoteModel> noteModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbManager = new DBManager(this);
        dbManager.open();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("HOME");
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.noteRecyclerView);

        noteModelArrayList = new ArrayList<>();
        noteModelArrayList = (ArrayList<NoteModel>) dbManager.getAllNotes();

        infoText = findViewById(R.id.infoText);

        if (!noteModelArrayList.isEmpty()) infoText.setVisibility(View.GONE);
        clickListener = this::popUpMenuOption;

        recyclerViewAdapter = new RecyclerViewAdapter(this, noteModelArrayList, clickListener);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        addNote = findViewById(R.id.addNote);
        addNote.setOnClickListener(view -> {
            Intent create_intent = new Intent(getApplicationContext(), CreateUpdateActivity.class);
            create_intent.putExtra("category_id", 0);
            create_intent.putExtra("id", dbManager.getAllNotes().isEmpty() ? 0 : dbManager.getAllNotes().size() - 1);

            startActivity(create_intent);
        });
    }

    private void popUpMenuOption(View view, NoteModel noteModel) {
        PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            if (menuItem.getTitle().equals("modify")) {
                Intent modify_intent = new Intent(getApplicationContext(), CreateUpdateActivity.class);
                modify_intent.putExtra("category_id", 1);
                modify_intent.putExtra("id", noteModel.getId());
                modify_intent.putExtra("title", noteModel.getTitle());
                modify_intent.putExtra("desc", noteModel.getContent());

                startActivity(modify_intent);
            } else {
                dbManager.deleteNote(noteModel);
                Toast.makeText(this, "item "+ (noteModel.getId() + 1) +" deleted", Toast.LENGTH_SHORT).show();
                reload();
            }
            return true;
        });

        popupMenu.show();
    }

    private void reload() {
        noteModelArrayList = new ArrayList<>();
        noteModelArrayList = (ArrayList<NoteModel>) dbManager.getAllNotes();

        infoText = findViewById(R.id.infoText);

        if (!noteModelArrayList.isEmpty()) infoText.setVisibility(View.GONE);
        clickListener = this::popUpMenuOption;

        recyclerViewAdapter = new RecyclerViewAdapter(this, noteModelArrayList, clickListener);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}