package com.example.watchly;

import android.app.ActivityOptions;
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
import java.util.Comparator;
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
    String type = "", title = "";
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
        findViewById(R.id.searchNew).setOnClickListener(v -> {
            pages.animateButton(v);
            Intent intent = new Intent(SearchListActivity.this, SearchActivity.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchListActivity.this);
            startActivity(intent, options.toBundle());
        });
        pages.setMenuIntent(findViewById(R.id.textWatchlist), findViewById(R.id.toWatch), WatchlistActivity.class);
        pages.setLogout(findViewById(R.id.logout));
        pages.setName(findViewById(R.id.name));

        Intent intent = getIntent();
        getGenres();
        setDefaultLanguages();
        if(intent.getStringExtra("type") != null){
            type = intent.getStringExtra("type");
            selectedGenres = intent.getStringArrayListExtra("genres");
            selectedLanguages = intent.getStringArrayListExtra("languages");
            sortOption = intent.getStringExtra("sort");
            title = intent.getStringExtra("title");
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
            movieSet.clear();
            loadMoreMovies();
        }
        else{
            type = "All";
            sortOption = "Popularity";
            sortSetting = "vote_count.desc";
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
            movieSet.clear();
            loadMoreMovies();
        }
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
    private void searchByTitle(String title) {
        String query = title.trim();
        apiService.multiSearch(apiKey, query, currentPageMovies, "movie,tv").enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Movie> fetchedMovies = response.body().getResults();
                    if (fetchedMovies != null && !fetchedMovies.isEmpty()) {
                        for (Movie movie : fetchedMovies) {
                            if (movie.getTitle() != null) {
                                movieSet.add(movie);
                            }
                        }
                    }
                    checkIfRequestsCompleted();
                }
            }
            @Override
            public void onFailure(Call<MovieResponse> call, Throwable throwable) {
                showLoading(false);
                Toast.makeText(SearchListActivity.this, "Błąd wyszukiwania: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadMovies(boolean isTopRated){
        Call<MovieResponse> call;
        if (isTopRated) {
            call = apiService.getTopRatedMovies(apiKey, currentPageMovies);
        } else {
            call = apiService.getMovies(apiKey, sortSetting, currentPageMovies);
        }
        call.enqueue(new Callback<MovieResponse>() {
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

    private void loadSeries(boolean isTopRated){
        Call<MovieResponse> call;
        if (isTopRated) {
            call = apiService.getTopRatedSeries(apiKey, currentPageSeries);
        } else {
            call = apiService.getSeries(apiKey, sortSetting, currentPageSeries);
        }
        call.enqueue(new Callback<MovieResponse>() {
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

    private void sort(List<Movie> newMovies) {
        Comparator<Movie> comparator = null;
        switch (sortOption) {
            case "Popularity":
                comparator = Comparator.comparingDouble(Movie::getVote_count).reversed();
                break;
            case "Average Rating":
                comparator = Comparator.comparingDouble(Movie::getVote_average).reversed();
                break;
            case "Release Date (Newest First)":
                comparator = Comparator.comparing(Movie::getDate).reversed();
                break;
            case "Release Date (Oldest First)":
                comparator = Comparator.comparing(Movie::getDate);
                break;
        }
        if (comparator != null) {
            newMovies.sort(comparator);
        }
    }

    private void checkIfRequestsCompleted() {
        isLoading--;
        if(isLoading == 0) {
            List<Movie> newMovies = new ArrayList<>(movieSet);
            int previous = movieAdapter.getItemCount();
            if (title == null && title.isEmpty()) {
                if (previous != 0) { //nie sortujemy juz posortowanych, aby unknac powtarzania sie (movies i series to dwie rozne listy w api)
                    List<Movie> sublist = new ArrayList<>(newMovies.subList(previous, newMovies.size()));
                    sort(sublist);
                    newMovies.subList(previous, newMovies.size()).clear();
                    newMovies.addAll(previous, sublist);
                } else {
                    sort(newMovies);
                }
            }
            movieAdapter.movies.clear();
            movieAdapter.movies.addAll(newMovies);
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
        boolean isTopRated = sortOption.equals("Average Rating");
        if (title != null && !title.isEmpty()) {
            isLoading++;
            searchByTitle(title);
            currentPageMovies++;
        } else {
            switch (type) {
                case "Movie":
                    isLoading++;
                    loadMovies(isTopRated);
                    currentPageMovies++;
                    break;
                case "Series":
                    isLoading++;
                    loadSeries(isTopRated);
                    currentPageSeries++;
                    break;
                case "All":
                    isLoading += 2;
                    loadMovies(isTopRated);
                    loadSeries(isTopRated);
                    currentPageMovies++;
                    currentPageSeries++;
                    break;
            }
        }
    }

    private void checkLanguageAndGenre(List<Movie> movies){
        if (selectedLanguages == null && selectedGenres == null) {
            return;
        }
        Iterator<Movie> iterator = movies.iterator();
        while (iterator.hasNext()) {
            Movie movie = iterator.next();
            boolean hasLanguage = selectedLanguages.isEmpty();
            boolean hasGenre = selectedGenres.isEmpty();
            if (!hasLanguage) {
                for (String language : selectedLanguages) {
                    if (Objects.equals(languages.get(language), movie.getOriginal_language())) {
                        hasLanguage = true;
                        break;
                    }
                }
            }
            if (!hasGenre && movie.getGenre_ids() != null) {
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
        if (movies.isEmpty()) {
            loadMoreMovies();
        }
    }
}
