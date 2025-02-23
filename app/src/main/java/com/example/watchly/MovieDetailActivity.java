package com.example.watchly;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetailActivity extends AppCompatActivity {

    private TextView movieTitle, movieDescription, releaseDate, type, language, popularity, voteAverage, voteCount, genres;
    private ImageView moviePoster;
    TmdbApiService apiService;
    String apiKey = BuildConfig.TMDB_API_KEY;

    private Pages pages = new Pages(this);
    private Movie movie;

    private Map<Integer, String> genreMap = new HashMap<>(); //lista gatunkow

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieTitle = findViewById(R.id.movieTitle);
        movieDescription = findViewById(R.id.movieDescription);
        moviePoster = findViewById(R.id.moviePoster);
        releaseDate = findViewById(R.id.movieReleaseDate);
        type = findViewById(R.id.movieType);
        language = findViewById(R.id.movieLanguage);
        popularity = findViewById(R.id.moviePopularity);
        voteAverage = findViewById(R.id.movieVoteAverage);
        voteCount = findViewById(R.id.movieVoteCount);
        genres = findViewById(R.id.movieGenres);

        Retrofit retrofit = new Retrofit.Builder() //api
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(TmdbApiService.class);

        movie = getIntent().getParcelableExtra("movie"); //pobieranie filmu z intent
        if (movie != null) {
            getGenres();
            displayMovieDetails(movie);
        } else {
            Toast.makeText(this, "Wrong movie id", Toast.LENGTH_SHORT).show();
        }

        String page = getIntent().getStringExtra("pageType");
        Class<?> context;
        if(page.equals("seen")){
            context = SeenActivity.class;
        }
        else{
            context = WatchlistActivity.class;
        }
        findViewById(R.id.close).setOnClickListener(v -> {
            pages.animateButton(v);
            Intent intent = new Intent(MovieDetailActivity.this, context);
            startActivity(intent);
            finish();
        });
    }

    private void displayMovieDetails(Movie movie) {
        movieTitle.setText(movie.getTitle());
        movieDescription.setText(movie.getOverview());
        releaseDate.setText("Release date: " + movie.getRelease_date());
        if(!movie.isVideo()){
            type.setText("Type: Movie");
        }
        else{
            type.setText("Type: Series");
        }
        language.setText("Original language: " + movie.getOriginal_language());
        popularity.setText("Popularity: " + movie.getPopularity());
        voteAverage.setText("⭐ " + movie.getVote_average());
        voteCount.setText("(" + movie.getVote_count() + " votes)");
        Glide.with(this)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPoster_path())
                .into(moviePoster);
    }

    private void displayGenres() {
        if (genreMap.isEmpty()) {
            genres.setText("Genres: -");
//            Log.d("GENRES 1", String.valueOf(genreMap.size()));
        }
        else {
            String movieGenres = "";
            if (movie.getGenre_ids() != null) {
                for (int id : movie.getGenre_ids()) {
                    if (genreMap.containsKey(id)) {
                        movieGenres += genreMap.get(id) + ", ";
                    }
                }
            }
            if (!movieGenres.isEmpty()) {
                movieGenres = movieGenres.substring(0, movieGenres.length() - 2);
                genres.setText("Genres: " + movieGenres);
//                Log.d("GENRES 3", String.valueOf(genreMap.size()) + movieGenres);
            } else {
                genres.setText("Genres: -");
//                Log.d("GENRES 4", String.valueOf(genreMap.size()) + movieGenres);
            }
        }
    }

    private void getGenres() {
        apiService.getGenres(apiKey).enqueue(new Callback<Genres>() {
            @Override
            public void onResponse(Call<Genres> call, Response<Genres> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Genres.Genre genre : response.body().getGenres()) {
                        genreMap.put(genre.getId(), genre.getName());
                    }
//                    Log.d("GENRES", String.valueOf(genreMap.size()));
                }
                displayGenres();
            }
            @Override
            public void onFailure(Call<Genres> call, Throwable throwable) {
                Toast.makeText(MovieDetailActivity.this, "Error loading genres", Toast.LENGTH_SHORT).show();
//                Log.d("GENRES FAIL", String.valueOf(genreMap.size()));
            }
        });
    }
}
