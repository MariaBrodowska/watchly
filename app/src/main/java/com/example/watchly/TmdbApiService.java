package com.example.watchly;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApiService {
    @GET("trending/all/week")
    Call<MovieResponse> getTrendingMovies(@Query("api_key") String apiKey);
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );
    @GET("trending/discover/movie")
    Call<MovieResponse> getMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("genre/movie/list")
    Call<Genres> getGenres(@Query("api_key") String apiKey);

    @GET("configuration/languages")
    Call<List<Language>> getLanguages(
            @Query("api_key") String apiKey,
            @Query("sort_by") String sort
    );

//    @GET("discover/movie")
//    Call<MovieResponse> getMovies(
//            @Query("api_key") String apiKey,
////            @Query("page") int page,
//            @Query("with_genres") String genres,
//            @Query("language") String language,
//            @Query("sort_by") String sort
//    );
//discover/movie
    @GET("discover/movie")
    Call<MovieResponse> getMovies(
            @Query("api_key") String apiKey,
            @Query("sort_by") String sort,
            @Query("page") int page
    );

    @GET("movie/top_rated")
    Call<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );

    @GET("discover/tv")
    Call<MovieResponse> getSeries(
            @Query("api_key") String apiKey,
            @Query("sort_by") String sort,
            @Query("page") int page
    );

    @GET("tv/top_rated")
    Call<MovieResponse> getTopRatedSeries(
            @Query("api_key") String apiKey,
            @Query("page") int page
    );
    @GET("search/multi")
    Call<MovieResponse> multiSearch(
            @Query("api_key") String apiKey,
            @Query("query") String query,
            @Query("page") int page,
            @Query("media_type") String media_type
    );
}
