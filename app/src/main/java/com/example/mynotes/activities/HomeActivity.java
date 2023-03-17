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
import android.content.SharedPreferences;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.mynotes.services.EncryptionService;


import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //    private DBManager dbManager;
    private FirebaseFirestore db;
    private FirebaseUser user;

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

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

//        dbManager = new DBManager(this);
//        dbManager.open();

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
//        noteModelArrayList = (ArrayList<NoteModel>) dbManager.getAllNotes();

        infoText = findViewById(R.id.infoText);

        clickListener = this::popUpMenuOption;

        recyclerViewAdapter = new RecyclerViewAdapter(this, noteModelArrayList, clickListener);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        /* Cryptography */
        SharedPreferences sharedPreferences = getSharedPreferences("Secret_Key", MODE_PRIVATE);
        String secretKeyStr = sharedPreferences.getString("key", "");

        // Just in case something nightmare happen
        if (secretKeyStr.equals("")){
            Intent backToRoot = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(backToRoot);
            finish();
        }

        String[] tmp = secretKeyStr.split(",");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (String a : tmp) {
            output.write(Integer.parseInt(a));
        }
        byte[] secretKeyByteArr = output.toByteArray();

        SecretKey secretKey = new SecretKeySpec(secretKeyByteArr, "AES");
        Log.d("secret key create update", Arrays.toString(secretKey.getEncoded()));

        DocumentReference documentReference = db.collection("User_Collection")
                .document(user != null ? user.getUid() : "");

        /* End Of Cryptography */

        documentReference
                .collection("Notes")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    noteModelArrayList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            String titleDec = "";
                            String contentDec = "";
                            try {
                                titleDec = EncryptionService.decryptMsg(d.getString("title"), secretKey);
                                contentDec = EncryptionService.decryptMsg(d.getString("content"), secretKey);
                            } catch (NoSuchAlgorithmException | NoSuchPaddingException |
                                     InvalidKeyException | InvalidParameterSpecException |
                                     IllegalBlockSizeException | BadPaddingException |
                                     UnsupportedEncodingException |
                                     InvalidAlgorithmParameterException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                            NoteModel noteModel = new NoteModel(
                                    d.getId(),
                                    titleDec,
                                    contentDec
                            );


                            noteModelArrayList.add(noteModel);
                        }
                        if (!noteModelArrayList.isEmpty()) infoText.setVisibility(View.GONE);
                        //recyclerViewAdapter.notifyDataSetChanged();
                        Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRangeChanged(0, noteModelArrayList.size());
                    }
                })
                .addOnFailureListener(e -> {
                    infoText.setVisibility(View.VISIBLE);
                    infoText.setText("No note has been found!");
                });

        addNote = findViewById(R.id.addNote);
        addNote.setOnClickListener(view -> {
            Intent create_intent = new Intent(getApplicationContext(), CreateUpdateActivity.class);
            create_intent.putExtra("category_id", 0);
//            create_intent.putExtra("id", dbManager.getAllNotes().isEmpty() ? 0 : dbManager.getAllNotes().size() - 1);
            create_intent.putExtra("id", noteModelArrayList.isEmpty() ? 0 : noteModelArrayList.size());
            startActivity(create_intent);
        });
    }

    private void popUpMenuOption(int position, View view, NoteModel noteModel) {
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
                db.collection("User_Collection")
                        .document(user != null ? user.getUid() : "")
                        .collection("Notes")
                        .document(noteModel.getId())
                        .delete()
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                            noteModelArrayList.remove(position);
                            Objects.requireNonNull(recyclerView.getAdapter()).notifyItemRemoved(position);
                            recyclerView.smoothScrollToPosition(position+1);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error deleting note", Toast.LENGTH_SHORT).show();
                        });
//                dbManager.deleteNote(noteModel);
//                Toast.makeText(this, "item " + (noteModel.getId() + 1) + " deleted", Toast.LENGTH_SHORT).show();
//                reload();
            }
            return true;
        });

        popupMenu.show();
    }

//    private void reload() {
//        noteModelArrayList = new ArrayList<>();
//        noteModelArrayList = (ArrayList<NoteModel>) dbManager.getAllNotes();
//
//        infoText = findViewById(R.id.infoText);
//
//        if (!noteModelArrayList.isEmpty()) infoText.setVisibility(View.GONE);
//        clickListener = this::popUpMenuOption;
//
//        recyclerViewAdapter = new RecyclerViewAdapter(this, noteModelArrayList, clickListener);
//        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
//
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setAdapter(recyclerViewAdapter);
//    }

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