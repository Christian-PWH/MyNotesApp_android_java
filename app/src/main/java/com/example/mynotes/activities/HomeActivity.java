package com.example.mynotes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotes.R;
import com.example.mynotes.adapters.GridViewAdapter;
import com.example.mynotes.models.NoteModel;
import com.example.mynotes.sqlite.DBManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private DBManager dbManager;
    Toolbar toolbar;
    GridView noteGridView;

    FloatingActionButton addNote;

    ImageView itemPopUp;

    Button button;

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

        noteGridView = findViewById(R.id.gridNote);
        noteGridView.setEmptyView(findViewById(R.id.infoText));

        noteModelArrayList = new ArrayList<>();
        noteModelArrayList = (ArrayList<NoteModel>) dbManager.getAllNotes();

        GridViewAdapter gridViewAdapter = new GridViewAdapter(this, noteModelArrayList);
        gridViewAdapter.notifyDataSetChanged();
        noteGridView.setAdapter(gridViewAdapter);

        noteGridView.setOnItemClickListener((adapterView, view, position, l) -> {
//            itemPopUp = view.findViewById(R.id.itemPopUp);
//            NoteModel noteModel = (NoteModel) adapterView.getItemAtPosition(position);
//
//            itemPopUp.setOnClickListener(viewDelete -> {
//
//                Toast.makeText(getApplicationContext(),"Masuk",Toast.LENGTH_SHORT).show();
//                dbManager.deleteNote(noteModel);
//                // Initializing the popup menu and giving the reference as current context
//                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, button);
//
//                // Inflating popup menu from popup_menu.xml file
//                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(menuItem -> {
//                    Toast.makeText(HomeActivity.this, "You Clicked " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
//                    return true;
//                });
//                // Showing the popup menu
//                popupMenu.show();
//            });
            TextView titleTextView = view.findViewById(R.id.itemTitle);
            TextView contentTextView = view.findViewById(R.id.itemContent);

            String title = titleTextView.getText().toString();
            String content = contentTextView.getText().toString();

            Intent modify_intent = new Intent(getApplicationContext(), CreateUpdateActivity.class);
            modify_intent.putExtra("category_id", 1);
            modify_intent.putExtra("id", position);
            modify_intent.putExtra("title", title);
            modify_intent.putExtra("desc", content);

            startActivity(modify_intent);
        });

        addNote = findViewById(R.id.addNote);
        addNote.setOnClickListener(view -> {
            Intent create_intent = new Intent(getApplicationContext(), CreateUpdateActivity.class);
            create_intent.putExtra("category_id", 0);
            create_intent.putExtra("id", dbManager.getAllNotes().isEmpty() ? 0 : dbManager.getAllNotes().size()-1);

            startActivity(create_intent);
        });
    }

    private void reload() {
        noteModelArrayList = new ArrayList<>();
        noteModelArrayList = (ArrayList<NoteModel>) dbManager.getAllNotes();
        GridViewAdapter gridViewAdapter = new GridViewAdapter(this, noteModelArrayList);
        gridViewAdapter.notifyDataSetChanged();
        noteGridView.setAdapter(gridViewAdapter);
    }

}