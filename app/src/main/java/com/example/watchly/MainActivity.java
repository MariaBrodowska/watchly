package com.example.watchly;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private List<Movie> movies;
    private MovieAdapter movieAdapter;
    private static final int MAX_PAGES = 100;
    private int currentPage = new Random().nextInt(MAX_PAGES) + 1;
    private boolean isLoading = false;
    private List<Movie> returnMovies;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirestoreManager fm = new FirestoreManager();
    private FirebaseAuth auth;
    private Pages pages = new Pages(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        SwipeFlingAdapterView swipeView = findViewById(R.id.swipeView);
        movies = new ArrayList<>();
        returnMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movies);
        swipeView.setAdapter(movieAdapter);
        swipeView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                movieAdapter.removeFirst();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                returnMovies.add(0,(Movie) dataObject);
            }
            @Override
            public void onRightCardExit(Object dataObject){
                Toast.makeText(MainActivity.this, "Added to watchlist!", Toast.LENGTH_SHORT).show();
                returnMovies.add(0,(Movie) dataObject);
                Movie topMovie = (Movie) dataObject;
                fm.addToWatchlist(topMovie);
            }
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (!isLoading) {
                    loadMoreMovies();
                }
            }
            @Override
            public void onScroll(float scrollProgressPercent) {}
        });

        findViewById(R.id.buttonSkip).setOnClickListener(v -> {
            pages.animateButton(v);
            swipeView.getTopCardListener().selectLeft();
        });
        findViewById(R.id.buttonSeen).setOnClickListener(v -> {
            pages.animateButton(v);
            Toast.makeText(MainActivity.this, "Added to watched!", Toast.LENGTH_SHORT).show();
            swipeView.getTopCardListener().selectLeft();
            Movie topMovie = movies.get(0);
            fm.addToWatched(topMovie);
        });
        findViewById(R.id.buttonAdd).setOnClickListener(v -> {
            pages.animateButton(v);
            swipeView.getTopCardListener().selectRight();
        });

        pages.setMenuIntent(findViewById(R.id.textSeen), findViewById(R.id.watched), SeenActivity.class);
        pages.setMenuIntent(findViewById(R.id.textDiscover), findViewById(R.id.discover), MainActivity.class);
        pages.setMenuIntent(findViewById(R.id.textSearch), findViewById(R.id.search), SearchListActivity.class);
        pages.setMenuIntent(findViewById(R.id.textWatchlist), findViewById(R.id.toWatch), WatchlistActivity.class);
        pages.setLogout(findViewById(R.id.logout));
        pages.setName(findViewById(R.id.name));

        findViewById(R.id.buttonReturn).setOnClickListener(v -> {
            pages.animateButton(v);
            if (!returnMovies.isEmpty()) {
                movies.add(0, returnMovies.get(0));
                returnMovies.remove(0);
                swipeView.removeAllViewsInLayout(); //usuniecie widokow
                swipeView.requestLayout(); //ponowne zaladowanie
            }
            else{
                Toast.makeText(MainActivity.this, "No card to undo!", Toast.LENGTH_SHORT).show();
            }
        });

        loadMoreMovies();
        swipeView.setOnItemClickListener((item, object) -> {
            Movie movie = movieAdapter.getItem(item);
            View convertView = swipeView.getSelectedView();
            View frontView = convertView.findViewById(R.id.frontLayout);
            View backView = convertView.findViewById(R.id.backLayout);
            if(!movie.isFlipped()) {
                movieAdapter.flipCard(convertView, frontView, backView);
            }
            else{
                movieAdapter.flipCard(convertView, backView, frontView);
            }
            movie.setFlipped(!movie.isFlipped());
        });
    }

    private void loadMoreMovies() {
        if (currentPage > MAX_PAGES) {
            currentPage = 1;
        }
        isLoading = true;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TmdbApiService apiService = retrofit.create(TmdbApiService.class);
        String apiKey = BuildConfig.TMDB_API_KEY;

        apiService.getPopularMovies(apiKey, currentPage).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> fetchedMovies = response.body().getResults();

                    if (!fetchedMovies.isEmpty()) {
                        movies.addAll(fetchedMovies);
                        movieAdapter.notifyDataSetChanged();
                        currentPage++;
                    } else {
                        currentPage = 1;
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Error loading movies", Toast.LENGTH_SHORT).show();
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "Connection error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                isLoading = false;
            }
        });
    }
}
