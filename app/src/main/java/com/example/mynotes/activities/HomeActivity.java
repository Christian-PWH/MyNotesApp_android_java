package com.example.mynotes.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mynotes.R;
import com.example.mynotes.adapters.RecyclerViewAdapter;
import com.example.mynotes.interfaces.ClickListener;
import com.example.mynotes.models.NoteModel;
import com.example.mynotes.sqlite.DBManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DBManager dbManager;
    private ClickListener clickListener;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    Toolbar toolbar;

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;

    TextView infoText;
    FloatingActionButton addNote;
    ArrayList<NoteModel> noteModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbManager = new DBManager(this);
        dbManager.open();

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

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
                modify_intent.putExtra("content", noteModel.getContent());

                startActivity(modify_intent);
            } else {
                dbManager.deleteNote(noteModel);
                Toast.makeText(this, "item " + (noteModel.getId() + 1) + " deleted", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_profile: {
                Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.nav_logout: {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
            break;

        }
        return true;
    }
}