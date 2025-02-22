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

public class RegisterActivity extends AppCompatActivity {
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
        setContentView(R.layout.activity_register);

        TextView login = findViewById(R.id.login);
        login.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
    }
}

    //        ImageView fb = findViewById(R.id.fb);
//        ImageView google = findViewById(R.id.google);
//        ImageView twitter = findViewById(R.id.twitter);
//        fb.setOnClickListener(v -> {
//        });
    //  }