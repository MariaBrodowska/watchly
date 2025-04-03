package com.example.watchly;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class GoogleSignInManager {
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private Context context;
    private static final int RC_SIGN_IN = 9001;  //request code logowania google
    private ActivityResultLauncher<Intent> activityResultLauncher;

    public GoogleSignInManager(Context context, ActivityResultLauncher<Intent> activityResultLauncher) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.activityResultLauncher = activityResultLauncher;
        this.firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.CLIENT_ID)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(context, options);
    }

    //obsluga wynikow logowania
    public void handleSignInResult(ActivityResult result) {
        Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
        try {
            GoogleSignInAccount signInAccount = accountTask.getResult();
            if (signInAccount != null) {
                FirebaseAuth.getInstance().signInWithCredential(
                        GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null) //przekazywanie tokenu z google do firebase
                ).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        saveUserToFirestore(user); //zapisanie uzytkownika
                        Toast.makeText(context, "Signed in successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Sign in failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Google sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    public GoogleSignInClient getGoogleSignInClient(){
        return googleSignInClient;
    }

    public int getRcSignIn(){
        return RC_SIGN_IN;
    }

    //zapisanie dsnych uzytkownika do firestore
    private void saveUserToFirestore(FirebaseUser user) {
        if (user == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> userData = new HashMap<>();
        userData.put("id", user.getUid());
        userData.put("name", user.getDisplayName());
        userData.put("email", user.getEmail());
        userData.put("profile", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");

        db.collection("users").document(user.getUid())
                .set(userData, SetOptions.merge())
                .addOnSuccessListener(aVoid -> {
//                    Log.d("Firestore", "User data saved.");
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