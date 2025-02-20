package com.example.watchly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView register = findViewById(R.id.registerNow);
        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        ImageView fb = findViewById(R.id.fb);
        ImageView google = findViewById(R.id.google);
        ImageView twitter = findViewById(R.id.twitter);
        fb.setOnClickListener(v -> {

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
