package com.example.watchly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.time.LocalDate;

public class SearchListActivity extends AppCompatActivity {

    private Pages pages = new Pages(this);
    private static final int MAX_PAGES = 950;
    private int currentPageMovies = 1;
    private int currentPageSeries = 1;
    private boolean loadingMore = false;
    private int isLoading = 0;
    private Set<Movie> movieSet = new LinkedHashSet<>();
    private MovieListAdapter movieAdapter;
    RecyclerView recyclerView;
    String type = "";
    List<String> selectedGenres = new ArrayList<>();
    List<String> selectedLanguages = new ArrayList<>();
    String sortOption = "";
    String sortSetting = "";
    private Map<Integer, String> genreMap = new HashMap<>();
    private HashMap<String,String> languages = new HashMap<>();

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    TmdbApiService apiService = retrofit.create(TmdbApiService.class);
    String apiKey = BuildConfig.TMDB_API_KEY;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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

        Intent intent = getIntent();
        getGenres();
        setDefaultLanguages();

        type = intent.getStringExtra("type");
        selectedGenres = intent.getStringArrayListExtra("genres");
        selectedLanguages = intent.getStringArrayListExtra("languages");
        sortOption = intent.getStringExtra("sort");
        if (sortOption != null) {
            switch (sortOption) {
                case "Popularity":
                    sortSetting = "vote_count.desc"; //discover/movie
                    break;
                case "Average Rating":
                    sortSetting = "vote_average.desc"; //top_rated
                    break;
                case "Release Date (Newest First)":
                    sortSetting = Objects.equals(type, "Movie") ? "release_date.desc" : "first_air_date.desc";
                    break;
                case "Release Date (Oldest First)":
                    sortSetting = Objects.equals(type, "Movie") ? "release_date.asc" : "first_air_date.asc";
                    break;
                default:
                    sortSetting = "vote_count.desc";
            }
        } else {
            sortSetting = "vote_count.desc";
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        movieAdapter = new MovieListAdapter(this, new ArrayList<>(movieSet), "search");
        recyclerView.setAdapter(movieAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == movieAdapter.getItemCount() - 1) {
                    if (!loadingMore && currentPageMovies <= MAX_PAGES && currentPageSeries <= MAX_PAGES) {
                        loadMoreMovies();
                    }
                }
            }
        });

//        Log.d("SearchListActivity", "Type: " + type);
//        Log.d("SearchListActivity", "Genres: " + selectedGenres);
//        Log.d("SearchListActivity", "Languages: " + selectedLanguages);
//        Log.d("SearchListActivity", "Sort: " + sortOption);
        movieSet.clear();
        loadMoreMovies();
    }
    private void showLoading(boolean show) {
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
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
    }
    private void getGenres() {
        apiService.getGenres(apiKey).enqueue(new Callback<Genres>() {
            @Override
            public void onResponse(Call<Genres> call, Response<Genres> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (Genres.Genre genre : response.body().getGenres()) {
                        genreMap.put(genre.getId(), genre.getName());
                    }
                }
            }
            @Override
            public void onFailure(Call<Genres> call, Throwable throwable) {
            }
        });
    }

    private void loadTopRatedMovies(){
        apiService.getTopRatedMovies(apiKey, currentPageMovies).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                loadingMore = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> fetchedMovies = response.body().getResults();
                    if (fetchedMovies == null || fetchedMovies.isEmpty()) {
                        loadingMore = false;
                        return;
                    }
                    checkLanguageAndGenre(fetchedMovies);
                    movieSet.addAll(fetchedMovies);
                    checkIfRequestsCompleted();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                loadingMore = false;
                Toast.makeText(SearchListActivity.this, "Connection error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadMovies(){
        apiService.getMovies(apiKey, sortSetting, currentPageMovies).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                loadingMore = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> fetchedMovies = response.body().getResults();
                    if (fetchedMovies == null || fetchedMovies.isEmpty()) {
                        loadingMore = false;
                        return;
                    }
                    checkLanguageAndGenre(fetchedMovies);
                    movieSet.addAll(fetchedMovies);
                    Log.d("SEARCH", "Loading movies, page:" + currentPageMovies);
                    checkIfRequestsCompleted();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                loadingMore = false;
                Toast.makeText(SearchListActivity.this, "Connection error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTopRatedSeries(){
        apiService.getTopRatedSeries(apiKey, currentPageSeries).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                loadingMore = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> fetchedMovies = response.body().getResults();

                    if (fetchedMovies == null || fetchedMovies.isEmpty()) {
                        loadingMore = false;
                        return;
                    }
                    checkLanguageAndGenre(fetchedMovies);
                    movieSet.addAll(fetchedMovies);
                    checkIfRequestsCompleted();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                loadingMore = false;
                Toast.makeText(SearchListActivity.this, "Connection error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSeries(){
        apiService.getSeries(apiKey, sortSetting, currentPageSeries).enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                loadingMore = false;
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> fetchedMovies = response.body().getResults();

                    if (fetchedMovies == null || fetchedMovies.isEmpty()) {
                        loadingMore = false;
                        return;
                    }
                    checkLanguageAndGenre(fetchedMovies);
                    movieSet.addAll(fetchedMovies);
                    Log.d("SEARCH", "Loading series, page:" + currentPageSeries);
                    checkIfRequestsCompleted();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                loadingMore = false;
                Toast.makeText(SearchListActivity.this, "Connection error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void sort(List<Movie> newMovies){
        switch (sortOption) {
            case "Popularity":
                newMovies.sort((m1, m2) -> Double.compare(m2.getVote_count(), m1.getVote_count()));
                break;
            case "Average Rating":
                newMovies.sort((m1, m2) -> Double.compare(m2.getVote_average(), m1.getVote_average()));
                break;
            case "Release Date (Newest First)":
                List<Movie> newList = new ArrayList<>();
                newMovies.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
                for (Movie movie : newMovies){
                    LocalDate movieDate = LocalDate.parse(movie.getDate(), formatter);
                    LocalDate localDate = LocalDate.now().plusYears(1);
                    if (movieDate.isBefore(localDate)){
                        newList.add(movie);
                    }
                }
                newMovies.clear();
                newMovies.addAll(newList);
                if(newList.isEmpty()){
                    loadMoreMovies();
                }
                break;
            case "Release Date (Oldest First)":
                newMovies.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
                break;
        }
    }

//    private void sort(List<Movie> newMovies) {
//        Comparator<Movie> comparator = null;
//
//        switch (sortOption) {
//            case "Popularity":
//                comparator = Comparator.comparingDouble(Movie::getVote_count).reversed();
//                break;
//            case "Average Rating":
//                comparator = Comparator.comparingDouble(Movie::getVote_average).reversed();
//                break;
//            case "Release Date (Newest First)":
//                comparator = Comparator.comparing(Movie::getDate).reversed();
//                break;
//            case "Release Date (Oldest First)":
//                comparator = Comparator.comparing(Movie::getDate);
//                break;
//        }
//
//        if (comparator != null) {
//            newMovies.sort(comparator);
//        }
//    }

    private void checkIfRequestsCompleted() {
        isLoading--;
        if(isLoading == 0) {
            List<Movie> newMovies = new ArrayList<>(movieSet);
            Log.d("SEARCH adapter", String.valueOf(movieAdapter.getItemCount()));

            Log.d("SEARCH movieSet", String.valueOf(movieSet));
            sort(newMovies);
            Log.d("SEARCH newMovies", String.valueOf(newMovies));
            int previous = movieAdapter.getItemCount();
            movieAdapter.movies.clear();
            movieAdapter.movies.addAll(newMovies);
            Log.d("SEARCH adapter", String.valueOf(movieAdapter.getItemCount()));
        movieAdapter.notifyItemRangeInserted(previous, newMovies.size());
            loadingMore = false;
            if(movieAdapter.getItemCount()>1)
                showLoading(false);
        }
    }
    private void loadMoreMovies() {
        if (loadingMore || currentPageMovies > MAX_PAGES || currentPageSeries > MAX_PAGES) {
            showLoading(false);
            return;
        }
        if(movieAdapter.getItemCount()<1)
            showLoading(true);
        loadingMore = true;
        isLoading = 0;
        switch(type){
            case "Movie":
                isLoading++;
                if (sortOption.equals("Average Rating")) {
                    loadTopRatedMovies();
                } else {
                    loadMovies();
                }
                currentPageMovies++;
                break;
            case "Series":
                isLoading++;
                if(sortOption.equals("Average Rating")){
                    loadTopRatedSeries();
                }
                else{
                    loadSeries();
                }
                currentPageSeries++;
                break;
            case "All":
                isLoading+=4;
                for(int i=0; i<2; i++) {
                    if (sortOption.equals("Average Rating")) {
                        loadTopRatedMovies();
                        loadTopRatedSeries();
                    } else {
                        loadMovies();
                        loadSeries();
                    }
                    currentPageMovies++;
                    currentPageSeries++;
                }
                break;
        }
    }

    private void checkLanguageAndGenre(List<Movie> movies){
        if(selectedLanguages==null && selectedGenres==null){
            return;
        }
        Iterator<Movie> iterator = movies.iterator();
        while (iterator.hasNext()) {
            Movie movie = iterator.next();
            boolean hasLanguage = selectedLanguages.isEmpty();
            boolean hasGenre = selectedGenres.isEmpty();
            if (selectedLanguages != null){
                for (String language : selectedLanguages){
                    if(Objects.equals(languages.get(language), movie.getOriginal_language())){
                        hasLanguage = true;
                        break;
                    }
                }
            }
            if (selectedGenres != null && movie.getGenre_ids() != null) {
                for (int id : movie.getGenre_ids()) {
                    if (selectedGenres.contains(genreMap.get(id))) {
                        hasGenre = true;
                        break;
                    }
                }
            }
            if (!hasLanguage || !hasGenre) {
                iterator.remove();
            }
        }
        if(movies.isEmpty()){
            loadMoreMovies();
        }
    }

}
