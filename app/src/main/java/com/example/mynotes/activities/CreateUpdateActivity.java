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

public class CreateUpdateActivity extends AppCompatActivity {

    private DBManager dbManager;
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

        dbManager = new DBManager(this);
        dbManager.open();

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
                if(editTitle.getText().toString() == null || editContent.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(),"Field is Empty!",Toast.LENGTH_SHORT).show();
                }
                dbManager.addNote(new NoteModel(id, editTitle.getText().toString(), editContent.getText().toString()));
                Intent newIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(newIntent);
                finish();
            });

        } else {
            toolbar.setTitle("Modify Note");
            setSupportActionBar(toolbar);

            int id = intent.getIntExtra("id", 0);
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");

            editTitle.setText(title);
            editContent.setText(content);
            createUpdateBtn.setText("Modify Note");
            createUpdateBtn.setOnClickListener(view -> {
                dbManager.updateNote(new NoteModel(id, editTitle.getText().toString(), editContent.getText().toString()));
                Intent newIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(newIntent);
                finish();
            });
        }
    }
}