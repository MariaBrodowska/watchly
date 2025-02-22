package com.example.watchly;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private void animateButton(View v) {
        ScaleAnimation scaleDown = new ScaleAnimation(
                1f, 0.8f, 1f, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setDuration(150);
        scaleDown.setFillAfter(true);
        ScaleAnimation scaleUp = new ScaleAnimation(
                0.8f, 1f, 0.8f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(100);
        scaleUp.setFillAfter(true);
        v.startAnimation(scaleDown);
        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                v.startAnimation(scaleUp);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    private void animateMenuButton(View button, int textViewId, int imageViewId) {
        TextView textView = findViewById(textViewId);
        ScaleAnimation scaleDown = new ScaleAnimation(
                1f, 0.9f, 1f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setDuration(150);

        scaleDown.setFillAfter(true);
        ObjectAnimator colorChange = ObjectAnimator.ofArgb(textView, "textColor",
                Color.parseColor("#FFFFFF"), Color.parseColor("#FF9800"));
        colorChange.setDuration(100);

        ScaleAnimation scaleUp = new ScaleAnimation(
                0.9f, 1f, 0.9f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(100);
        scaleUp.setFillAfter(true);
        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(scaleUp);
                colorChange.reverse();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        button.startAnimation(scaleDown);
        colorChange.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView name = findViewById(R.id.name);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            name.setText(userName);
        } else {
            name.setText("Guest");
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
//                Toast.makeText(MainActivity.this, "Skip", Toast.LENGTH_SHORT).show();
                returnMovies.add(0,(Movie) dataObject);
            }

            @Override
            public void onRightCardExit(Object dataObject){
                Toast.makeText(MainActivity.this, "Added to watchlist!", Toast.LENGTH_SHORT).show();
                returnMovies.add(0,(Movie) dataObject);
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
        findViewById(R.id.logout).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), StartActivity.class));
            finish();
        });
        findViewById(R.id.buttonSkip).setOnClickListener(v -> {
            animateButton(v);
            swipeView.getTopCardListener().selectLeft();
        });
        findViewById(R.id.buttonSeen).setOnClickListener(v -> {
            animateButton(v);
            Toast.makeText(MainActivity.this, "Added to watched!", Toast.LENGTH_SHORT).show();
            swipeView.getTopCardListener().selectLeft();
        });
        findViewById(R.id.buttonAdd).setOnClickListener(v -> {
            animateButton(v);
            swipeView.getTopCardListener().selectRight();
        });
        findViewById(R.id.watched).setOnClickListener(v -> {
            animateMenuButton(v, R.id.textSeen, R.id.watched);
        });
        findViewById(R.id.discover).setOnClickListener(v -> {
            animateMenuButton(v, R.id.textDiscover, R.id.discover);
        });
        findViewById(R.id.search).setOnClickListener(v -> {
            animateMenuButton(v, R.id.textSearch, R.id.search);
        });
        findViewById(R.id.toWatch).setOnClickListener(v -> {
            animateMenuButton(v, R.id.textWatchlist, R.id.toWatch);
        });
        findViewById(R.id.buttonReturn).setOnClickListener(v -> {
            animateButton(v);
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

        loadMoreMovies(); //pierwsza strona
        swipeView.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int item, Object object) {
//                Log.d("MovieAdapter", "KLIKNIETO W MAIN!");
                Movie movie = movieAdapter.getItem(item);
                View convertView = swipeView.getSelectedView(); //widok zaznaczonej karty
                View frontView = convertView.findViewById(R.id.frontLayout);
                View backView = convertView.findViewById(R.id.backLayout);
                if(!movie.isFlipped()) {
                    movieAdapter.flipCard(convertView, frontView, backView);
                }
                else{
                    movieAdapter.flipCard(convertView, backView, frontView);
                }
                movie.setFlipped(!movie.isFlipped());
            }
        });
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
