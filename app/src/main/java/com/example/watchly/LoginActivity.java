package com.example.watchly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInManager googleSignInManager;

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

        TextView register = findViewById(R.id.registerNow);
        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        googleSignInManager = new GoogleSignInManager(this, activityResultLauncher);

        googleSignInManager.getGoogleSignInClient().signOut().addOnCompleteListener(this, task -> {
            ImageView signInButton = findViewById(R.id.google);
            signInButton.setOnClickListener(v -> {
                Intent intent = googleSignInManager.getGoogleSignInClient().getSignInIntent();
                activityResultLauncher.launch(intent);
            });
        });
//        TextView email = findViewById(R.id.email);
//        TextView password = findViewById(R.id.password);
//        TextView loginbtn = findViewById(R.id.buttonLogin);
//
//        loginbtn.setOnClickListener(v -> {
//            if(email.getText().toString().equals("admin") && password.getText().toString().equals("admin")){
//                Toast.makeText(ActivityLogin.this, "login successful", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(ActivityLogin.this, "login failed", Toast.LENGTH_SHORT).show();
//            }
//            });

    }
}
