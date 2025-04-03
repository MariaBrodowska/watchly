package com.example.watchly;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInManager googleSignInManager;
    private FirebaseAuth auth;

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
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        TextView loginbtn = findViewById(R.id.buttonLogin);

        TextView register = findViewById(R.id.registerNow);
        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        googleSignInManager = new GoogleSignInManager(this, activityResultLauncher);

        loginbtn.setOnClickListener(v -> {
           String emailText = email.getText().toString().trim();
           String passwordText = password.getText().toString().trim();
           if(emailText.isEmpty() || passwordText.isEmpty()){
               Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show();
               return;
           }
            auth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
                    }
                });
        });

        googleSignInManager.getGoogleSignInClient().signOut().addOnCompleteListener(this, task -> {
            ImageView signInButton = findViewById(R.id.google);
            signInButton.setOnClickListener(v -> {
                Intent intent = googleSignInManager.getGoogleSignInClient().getSignInIntent();
                activityResultLauncher.launch(intent);
            });
        });

    }
}
