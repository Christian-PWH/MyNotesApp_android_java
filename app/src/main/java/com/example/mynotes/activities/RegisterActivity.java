package com.example.mynotes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mynotes.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.mynotes.services.EncryptionService;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailTextView, passwordTextView;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;

    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        emailTextView = findViewById(R.id.registerEmail);
        passwordTextView = findViewById(R.id.registerPass);
        Button registerBtn = findViewById(R.id.registerBtn);
        progressbar = findViewById(R.id.progressbarRegister);

        registerBtn.setOnClickListener(view -> registerNewUser());

    }

    private void registerNewUser() {
        progressbar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTextView.getText().toString().trim();
        password = passwordTextView.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            progressbar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),
                            "Please enter email!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            progressbar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),
                            "Please enter password!!",
                            Toast.LENGTH_LONG)
                    .show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                        "Registration successful!",
                                        Toast.LENGTH_LONG)
                                .show();
                        progressbar.setVisibility(View.GONE);

                        user = FirebaseAuth.getInstance().getCurrentUser();
                        db = FirebaseFirestore.getInstance();
//                        Log.d("secret key", task.getResult().toString());
                        SecretKey secretKey;
                        try {
                            secretKey = EncryptionService.generateKey("this is secret key");
                        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }

//                        String secretKeyStr = new String(secretKey.getEncoded());
                        StringBuilder secretKeyStr = new StringBuilder();
                        byte[] secretKeyByteArr = secretKey.getEncoded();

                        for (byte b : secretKeyByteArr) {
                            secretKeyStr.append(String.valueOf(b + ","));
                        }

                        Map<String, String> secretKeyMap = new HashMap<>();
                        secretKeyMap.put("Secret_Key", secretKeyStr.toString());

//                        Log.d("secret key1", secretKey.toString());
//                        Log.d("secret key2", secretKey.getFormat());
//                        Log.d("secret key3", secretKey.getAlgorithm());
//                        Log.d("secret key4", Arrays.toString(secretKey.getEncoded()));

//                        String a = Arrays.toString(secretKey.getEncoded());
//                        byte[] b = a.getBytes();

                        // Save Secret Key To Firebase
                        DocumentReference docRef = db.collection("User_Collection")
                                .document(user != null ? user.getUid() : "");
                        docRef
                                .collection("Secret_Key")
                                .add(secretKeyMap)
                                .addOnCompleteListener(documentReference -> {
                                    Log.d("secret key", task.getResult().toString());
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(
                                                    getApplicationContext(),
                                                    e.toString(),
                                                    Toast.LENGTH_LONG)
                                            .show();
                                });

                        // Save Secret Key Locally (Shared Pref)
                        SharedPreferences sharedPreferences = getSharedPreferences("Secret_Key", MODE_PRIVATE);
                        SharedPreferences.Editor edit = sharedPreferences.edit();
                        edit.putString("key", secretKeyStr.toString());
                        edit.apply();

                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(
                                        getApplicationContext(),
                                        "Registration failed!!"
                                                + " Please try again",
                                        Toast.LENGTH_LONG)
                                .show();

                        progressbar.setVisibility(View.GONE);
                    }
                });
    }


}