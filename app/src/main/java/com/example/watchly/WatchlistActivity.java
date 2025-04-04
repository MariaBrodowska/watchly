package com.example.watchly;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class WatchlistActivity extends AppCompatActivity {
    private Pages pages = new Pages(this);
    private RecyclerView recyclerView;
    private List<Movie> toWatchMovies = new ArrayList<>();
    private MovieListAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirestoreManager fm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        pages.setMenuIntent(findViewById(R.id.textSeen), findViewById(R.id.watched), SeenActivity.class);
        pages.setMenuIntent(findViewById(R.id.textDiscover), findViewById(R.id.discover), MainActivity.class);
        pages.setMenuIntent(findViewById(R.id.textSearch), findViewById(R.id.search), SearchListActivity.class);
        pages.setMenuIntent(findViewById(R.id.textWatchlist), findViewById(R.id.toWatch), WatchlistActivity.class);
        pages.setLogout(findViewById(R.id.logout));
        pages.setName(findViewById(R.id.name));

        recyclerView = findViewById(R.id.toWatchMovies);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();
        fm = new FirestoreManager();

        fm.getWatchlist(movies -> {
            toWatchMovies.clear();
            toWatchMovies.addAll(movies);
            Log.d("DO OBEJRZENIA WATCHLIST", "DO OBEJRZENIA: " + toWatchMovies.size());
            adapter = new MovieListAdapter(WatchlistActivity.this, toWatchMovies, "watchlist");
            recyclerView.setAdapter(adapter);
        });
    }
}
