package com.example.mynotes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mynotes.R;
import com.example.mynotes.adapters.RecyclerViewAdapter;
import com.example.mynotes.models.NoteModel;
import com.example.mynotes.sqlite.DBManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private DBManager dbManager;
    Toolbar toolbar;
    RecyclerView recyclerView;

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

        recyclerView = findViewById(R.id.noteRecyclerView);

        noteModelArrayList = new ArrayList<>();
        noteModelArrayList = (ArrayList<NoteModel>) dbManager.getAllNotes();

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(noteModelArrayList, this);

        GridLayoutManager layoutManager=new GridLayoutManager(this,2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerViewAdapter.setOnItemClickListener(noteModel -> {

        });

//        recyclerView.setOnItemClickListener((adapterView, view, position, l) -> {
//            itemPopUp = view.findViewById(R.id.itemPopUp);
//            NoteModel noteModel = (NoteModel) adapterView.getItemAtPosition(position);
//
//            itemPopUp.setOnClickListener(viewDelete -> {
//
//                Toast.makeText(getApplicationContext(),"in",Toast.LENGTH_SHORT).show();
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
//            TextView titleTextView = view.findViewById(R.id.itemTitle);
//            TextView contentTextView = view.findViewById(R.id.itemContent);
//
//            String title = titleTextView.getText().toString();
//            String content = contentTextView.getText().toString();
//
//            Intent modify_intent = new Intent(getApplicationContext(), CreateUpdateActivity.class);
//            modify_intent.putExtra("category_id", 1);
//            modify_intent.putExtra("id", position);
//            modify_intent.putExtra("title", title);
//            modify_intent.putExtra("desc", content);
//
//            startActivity(modify_intent);
//        });

        addNote = findViewById(R.id.addNote);
        addNote.setOnClickListener(view -> {
            Intent create_intent = new Intent(getApplicationContext(), CreateUpdateActivity.class);
            create_intent.putExtra("category_id", 0);
            create_intent.putExtra("id", dbManager.getAllNotes().isEmpty() ? 0 : dbManager.getAllNotes().size()-1);

            startActivity(create_intent);
        });
    }
}