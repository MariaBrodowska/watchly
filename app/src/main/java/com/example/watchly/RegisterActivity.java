package com.example.watchly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private GoogleSignInManager googleSignInManager;
    private FirebaseAuth auth;
    String nameText = "";
    Context context;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {
                        googleSignInManager.handleSignInResult(result);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        this.context = context;
        auth = FirebaseAuth.getInstance();
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        EditText name = findViewById(R.id.name);
        TextView registerbtn = findViewById(R.id.buttonRegister);

        TextView login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        registerbtn.setOnClickListener(v -> {
            String emailText = email.getText().toString().trim();
            String passwordText = password.getText().toString().trim();
            nameText = name.getText().toString().trim();
            if(emailText.isEmpty() || passwordText.isEmpty() || nameText.isEmpty()){
                Toast.makeText(this, "Name, email and password are required", Toast.LENGTH_SHORT).show();
                return;
            }
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            saveUserToFirestore(user);
                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        googleSignInManager = new GoogleSignInManager(this, activityResultLauncher);
        googleSignInManager.getGoogleSignInClient().signOut().addOnCompleteListener(this, task -> {
            ImageView signInButton = findViewById(R.id.google);
            signInButton.setOnClickListener(v -> {
                Intent intent = googleSignInManager.getGoogleSignInClient().getSignInIntent();
                activityResultLauncher.launch(intent);
            });
        });
    }
    private void saveUserToFirestore(FirebaseUser user) {
        if (user == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("id", user.getUid());
        userData.put("name", nameText);
        userData.put("email", user.getEmail());

        db.collection("users").document(user.getUid())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    if (context instanceof RegisterActivity) {
                        ((RegisterActivity) context).finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error saving user data", Toast.LENGTH_SHORT).show();
                });
    }
}