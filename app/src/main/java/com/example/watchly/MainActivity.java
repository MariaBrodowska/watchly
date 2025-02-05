package com.example.watchly;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {
    interface RequestDiscover{
        @GET("discover/movie")
        Call<MovieResponse> getMovies(@Query("api_key") String apiKey);
    }
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView = findViewById(R.id.result);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestDiscover requestDiscover = retrofit.create(RequestDiscover.class);
        String apiKey = BuildConfig.TMDB_API_KEY;
        requestDiscover.getMovies(apiKey).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                if (response.isSuccessful()) {
                    List<Movie> movies = response.body().getResults();
                    String titleList = "";
                    for(Movie movie : movies) titleList += movie.getTitle() + "\n";
                    titleList += String.valueOf(movies.size());
//                    textView.setText(response.body().getResults().get(3).getTitle());
                    textView.setText(titleList);
                } else {
                    textView.setText("Błąd: " + response.code() + ", " + response.message());
                    Log.e("API Error", "Error body: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                textView.setText("Błąd połączenia: " + throwable.getMessage());
            }
        });
    }
}