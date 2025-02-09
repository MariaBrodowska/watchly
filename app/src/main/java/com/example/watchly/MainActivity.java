package com.example.watchly;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeFlingAdapterView swipeView = findViewById(R.id.swipeView);
        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movies);
        swipeView.setAdapter(movieAdapter);

        swipeView.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                movieAdapter.removeFirst();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {}

            @Override
            public void onRightCardExit(Object dataObject) {}

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (!isLoading) {
                    loadMoreMovies();
                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {}
        });

        loadMoreMovies(); //pierwsza strona
    }

    private void loadMoreMovies() {
        if (currentPage > MAX_PAGES) {
            currentPage = 1; //reset
        }

        isLoading = true; //pobieranie

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TmdbApiService apiService = retrofit.create(TmdbApiService.class);
        String apiKey = BuildConfig.TMDB_API_KEY;

        apiService.getPopularMovies(apiKey, currentPage).enqueue(new Callback<MovieResponse>() {
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
