package com.example.watchly;

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
}
