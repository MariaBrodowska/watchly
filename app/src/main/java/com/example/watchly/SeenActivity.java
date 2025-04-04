package com.example.watchly;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SeenActivity extends AppCompatActivity{
    private Pages pages = new Pages(this);
    private RecyclerView recyclerView;
    private List<Movie> seenMovies = new ArrayList<>();
    private MovieListAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirestoreManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seen);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        pages.setMenuIntent(findViewById(R.id.textSeen), findViewById(R.id.watched), SeenActivity.class);
        pages.setMenuIntent(findViewById(R.id.textDiscover), findViewById(R.id.discover), MainActivity.class);
        findViewById(R.id.search).setOnClickListener(v -> {
            pages.animateButton(v);
            Intent intent = new Intent(SeenActivity.this, SearchActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SeenActivity.this);
            startActivity(intent, options.toBundle());
        });
        pages.setMenuIntent(findViewById(R.id.textWatchlist), findViewById(R.id.toWatch), WatchlistActivity.class);
        pages.setLogout(findViewById(R.id.logout));
        pages.setName(findViewById(R.id.name));

        recyclerView = findViewById(R.id.watchedMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        fm = new FirestoreManager();

        fm.getWatched(movies -> {
            seenMovies.clear();
            seenMovies.addAll(movies); //resetujemy
//                Log.d("OBEJRZANE SEEN", "OBEJRZANE: " + seenMovies.size());
            adapter = new MovieListAdapter(SeenActivity.this, seenMovies, "seen");
            recyclerView.setAdapter(adapter);
        });
    }
}
