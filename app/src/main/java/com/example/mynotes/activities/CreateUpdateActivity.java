package com.example.mynotes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynotes.R;
import com.example.mynotes.models.NoteModel;
import com.example.mynotes.sqlite.DBManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.mynotes.services.EncryptionService;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

        /* Cryptography */
        SharedPreferences sharedPreferences = getSharedPreferences("Secret_Key", MODE_PRIVATE);
        String secretKeyStr = sharedPreferences.getString("key", "");

        // Just in case something nightmare happen
        if (secretKeyStr.equals("")){
            Intent backToRoot = new Intent(CreateUpdateActivity.this, LoginActivity.class);
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
        /* End Of Cryptography */

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

//        dbManager = new DBManager(this);
//        dbManager.open();

        toolbar = findViewById(R.id.toolbar);
        editTitle = findViewById(R.id.editTitle);
        editContent = findViewById(R.id.editContent);
        createUpdateBtn = findViewById(R.id.createUpdateBtn);

        // Create Note
        if (category_id == 0) {
            toolbar.setTitle("Create Note");
            setSupportActionBar(toolbar);

            int id = intent.getIntExtra("id", 0);
            createUpdateBtn.setText("Create Note");
            createUpdateBtn.setOnClickListener(view -> {
                if (editTitle.getText().toString() == null || editContent.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "Field is Empty!", Toast.LENGTH_SHORT).show();
                }
                String titleEnc = "";
                String contentEnc = "";
                try {
                    titleEnc = EncryptionService.encryptMsg(editTitle.getText().toString(), secretKey);
                    contentEnc = EncryptionService.encryptMsg(editContent.getText().toString(), secretKey);
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                         InvalidParameterSpecException | IllegalBlockSizeException |
                         BadPaddingException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                Log.d("secret key", titleEnc);
                Log.d("secret key", contentEnc);
                Map<String, Object> note = new HashMap<>();
                note.put("id", id);
                note.put("title", titleEnc);
                note.put("content", contentEnc);

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

        }
        // Mofidy Note
        else {
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