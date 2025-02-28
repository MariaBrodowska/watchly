package com.example.watchly;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SearchListActivity extends AppCompatActivity {

    private Pages pages = new Pages(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);

        pages.setMenuIntent(findViewById(R.id.textSeen), findViewById(R.id.watched), SeenActivity.class);
        pages.setMenuIntent(findViewById(R.id.textDiscover), findViewById(R.id.discover), MainActivity.class);
        pages.setMenuIntent(findViewById(R.id.textSearch), findViewById(R.id.search), SearchListActivity.class);
        pages.setMenuIntent(findViewById(R.id.textWatchlist), findViewById(R.id.toWatch), WatchlistActivity.class);
        pages.setLogout(findViewById(R.id.logout));
        pages.setName(findViewById(R.id.name));

    }
}
