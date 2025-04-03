package com.example.watchly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Pages pages = new Pages(this);
    private List<String> genreList = new ArrayList<>();
    private HashMap<String,String> languages = new HashMap<>();
    List<String> languageList = new ArrayList<>();
    TmdbApiService apiService;
    String apiKey = BuildConfig.TMDB_API_KEY;
    private String[] options = {
            "Popularity", "Average Rating", "Release Date (Newest First)", "Release Date (Oldest First)"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //tylko jeden zaznaczony
        RadioButton[] radioButtons = {findViewById(R.id.allTypesButton), findViewById(R.id.movieButton), findViewById(R.id.seriesButton)};
        for(RadioButton button : radioButtons){
            button.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                if (isChecked){
                    button.setBackgroundResource(R.drawable.search_movie_checked);
                    for(RadioButton other : radioButtons){
                        if(other != button){
                            other.setChecked(false);
                            other.setBackgroundResource(R.drawable.search_movie_unchecked);
                        }
                    }
                }
            }));
        }
        Retrofit retrofit = new Retrofit.Builder() //api
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(TmdbApiService.class);

        Spinner spin = findViewById(R.id.sortSpinner);
        spin.setOnItemSelectedListener(this);
        ArrayAdapter<String> ad = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(ad);

        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(v -> {
            radioButtons[0].setChecked(true);
            for (RadioButton button : radioButtons) {
                button.setBackgroundResource(R.drawable.search_movie_unchecked);
            }
            radioButtons[0].setBackgroundResource(R.drawable.search_movie_checked);

            RecyclerView genreRecyclerView = findViewById(R.id.genreRecyclerView);
            GenreAdapter genreAdapter = (GenreAdapter) genreRecyclerView.getAdapter();
            if (genreAdapter != null) {
                genreAdapter.clear();}
            RecyclerView languageRecyclerView = findViewById(R.id.languageRecyclerView);
            LanguageAdapter languageAdapter = (LanguageAdapter) languageRecyclerView.getAdapter();
            if (languageAdapter != null) {
                languageAdapter.clear();
            }
            spin.setSelection(0);
        });

        setDefaultLanguages();
        getGenres();
        ImageView search = findViewById(R.id.searchButton);
        search.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, SearchListActivity.class);
            for (RadioButton button : radioButtons){
                if (button.isChecked()){
                    intent.putExtra("type", button.getText().toString());
                    break;
                }
            }
            RecyclerView genreRecyclerView = findViewById(R.id.genreRecyclerView);
            GenreAdapter adapter = (GenreAdapter) genreRecyclerView.getAdapter();
            if (adapter != null) {
                intent.putStringArrayListExtra("genres", new ArrayList<>(adapter.getSelectedGenres()));
            }
            RecyclerView languageRecyclerView = findViewById(R.id.languageRecyclerView);
            LanguageAdapter adapterLang = (LanguageAdapter) languageRecyclerView.getAdapter();
            if (adapterLang != null) {
                intent.putStringArrayListExtra("languages", new ArrayList<>(adapterLang.getSelectedLanguages()));
            }
            intent.putExtra("sort", spin.getSelectedItem().toString());
            EditText text = findViewById(R.id.titleText);
            intent.putExtra("title", text.getText().toString());
            SearchActivity.this.startActivity(intent);
        });
    }

    private void setDefaultLanguages() {
        languages.put("English", "en");
        languages.put("Spanish", "es");
        languages.put("French", "fr");
        languages.put("German", "de");
        languages.put("Chinese", "zh");
        languages.put("Japanese", "ja");
        languages.put("Russian", "ru");
        languages.put("Italian", "it");
        languages.put("Portuguese", "pt");
        languages.put("Hindi", "hi");
        languages.put("Arabic", "ar");
        languages.put("Korean", "ko");
        languages.put("Turkish", "tr");
        languages.put("Dutch", "nl");
        languages.put("Swedish", "sv");
        languages.put("Polish", "pl");
        languages.put("Danish", "da");
        languages.put("Finnish", "fi");
        languages.put("Norwegian", "no");
        languages.put("Hebrew", "he");
        languageList = new ArrayList<>(languages.keySet());
//        Log.d("LISTA", languageList.toString());
        setLanguageRecyclerView();
    }
    private void setGenreRecyclerView() {
        RecyclerView genreRecyclerView = findViewById(R.id.genreRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        genreRecyclerView.setLayoutManager(layoutManager);
        GenreAdapter adapter = new GenreAdapter(this, genreList);
        genreRecyclerView.setAdapter(adapter);
    }
    private void setLanguageRecyclerView() {
        RecyclerView genreRecyclerView = findViewById(R.id.languageRecyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        genreRecyclerView.setLayoutManager(layoutManager);
//        Log.d("LISTA", languageList.toString());
        LanguageAdapter adapter = new LanguageAdapter(this, languageList);
        genreRecyclerView.setAdapter(adapter);
    }
    private void getGenres() {
        apiService.getGenres(apiKey).enqueue(new Callback<Genres>() {
            @Override
            public void onResponse(Call<Genres> call, Response<Genres> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Genres.Genre genre : response.body().getGenres()) {
                        genreList.add(genre.getName());
//                        Log.d("GENRES", genre.getName());
                    }
//                    Log.d("GENRES", String.valueOf(genreList.size()));
                    setGenreRecyclerView();
                }
            }
            @Override
            public void onFailure(Call<Genres> call, Throwable throwable) {
                Toast.makeText(SearchActivity.this, "Error loading genres", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getLanguages(){
        apiService.getLanguages(apiKey, "vote_count.desc").enqueue(new Callback<List<Language>>() {
            @Override
            public void onResponse(Call<List<Language>> call, Response<List<Language>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Language language : response.body()) {
                        languageList.add(language.getEnglishName());
//                        Log.d("LANGUAGES", language.getEnglishName());
                    }
//                    Log.d("LANGUAGES", String.valueOf(languageList.size()));
                    setLanguageRecyclerView();
                }
            }
            @Override
            public void onFailure(Call<List<Language>> call, Throwable throwable) {
//                Log.e("LANGUAGES", throwable);
                Toast.makeText(SearchActivity.this, "Error loading languages", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
