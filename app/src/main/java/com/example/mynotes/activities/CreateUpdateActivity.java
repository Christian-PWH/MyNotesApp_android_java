package com.example.mynotes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynotes.R;
import com.example.mynotes.models.NoteModel;
import com.example.mynotes.sqlite.DBManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateUpdateActivity extends AppCompatActivity {

    private FirebaseFirestore db;

//    private DBManager dbManager;
    Toolbar toolbar;

    EditText editTitle;
    EditText editContent;
    Button createUpdateBtn;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_update);

        Intent intent = getIntent();
        int category_id = intent.getIntExtra("category_id", 0);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

//        dbManager = new DBManager(this);
//        dbManager.open();

        toolbar = findViewById(R.id.toolbar);
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        createUpdateBtn = findViewById(R.id.createUpdateBtn);


        if (category_id == 0) {
            toolbar.setTitle("Create Note");
            setSupportActionBar(toolbar);

            int id = intent.getIntExtra("id", 0);
            createUpdateBtn.setText("Create Note");
            createUpdateBtn.setOnClickListener(view -> {
                if (editTitle.getText().toString() == null || editContent.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "Field is Empty!", Toast.LENGTH_SHORT).show();
                }

                Map<String, Object> note = new HashMap<>();
                note.put("id", id);
                note.put("title", editTitle.getText().toString());
                note.put("content", editContent.getText().toString());

                if(user != null) {
                    db.collection("User_Collection")
                            .document(user.getUid())
                            .collection("Notes")
                            .add(note).addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Item " + (id + 1) + " created", Toast.LENGTH_SHORT).show();
                                Intent newIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(newIntent);
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }

//                dbManager.addNote(new NoteModel(id, editTitle.getText().toString(), editContent.getText().toString()));

//                Toast.makeText(this, "Item " + (id + 1) + " modified", Toast.LENGTH_SHORT).show();
//                Intent newIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(newIntent);
//                finish();
            });

        } else {
            toolbar.setTitle("Modify Note");
            setSupportActionBar(toolbar);

            String id = intent.getStringExtra("id");
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");

            editTitle.setText(title);
            editContent.setText(content);
            createUpdateBtn.setText("Modify Note");
            createUpdateBtn.setOnClickListener(view -> {
//                dbManager.updateNote(new NoteModel(id, editTitle.getText().toString(), editContent.getText().toString()));
                Map<String, Object> note = new HashMap<>();
                note.put("id", id);
                note.put("title", editTitle.getText().toString());
                note.put("content", editContent.getText().toString());

                if(user != null) {
                    db.collection("User_Collection")
                            .document(user.getUid())
                            .collection("Notes")
                            .document(id)
                            .set(note).addOnSuccessListener(documentReference -> {
                                Toast.makeText(this, "Note Modified", Toast.LENGTH_SHORT).show();
                                Intent newIntent = new Intent(getApplicationContext(), HomeActivity.class);
                                startActivity(newIntent);
                                finish();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                }

//                Toast.makeText(this, "Note Modified", Toast.LENGTH_SHORT).show();
//                Intent newIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(newIntent);
//                finish();
            });
        }
    }
}