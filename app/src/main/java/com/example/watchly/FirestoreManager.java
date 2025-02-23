package com.example.watchly;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreManager {
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private List<Movie> watchedMovies = new ArrayList<>();
    private List<Movie> toWatchMovies;

    public FirestoreManager() {
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private Map<String, Object> getMovieData(Movie movie) {
        Map<String, Object> movieData = new HashMap<>();
        movieData.put("id", movie.getId());
        movieData.put("title", movie.getTitle());
        movieData.put("poster_path", movie.getPoster_path());
        movieData.put("release_date", movie.getRelease_date());
        movieData.put("vote_average", movie.getVote_average());
        movieData.put("vote_count", movie.getVote_count());
        movieData.put("popularity", movie.getPopularity());
        movieData.put("original_language", movie.getOriginal_language());
        movieData.put("original_title", movie.getOriginal_title());
        movieData.put("overview", movie.getOverview());
        movieData.put("adult", movie.isAdult());
        movieData.put("video", movie.isVideo());
        movieData.put("backdrop_path", movie.getBackdrop_path());
        movieData.put("genre_ids", movie.getGenre_ids());
        return movieData;
    }

    public void addToWatchlist(Movie movie) {
        if (currentUser != null) {
            Map<String, Object> movieData = getMovieData(movie);
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("watchlist")
                    .document(String.valueOf(movie.getId()))
                    .set(movieData)
                    .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Film dodany do obejrzenia!"))
                    .addOnFailureListener(e -> Log.e("FIRESTORE", "Błąd zapisu do Firestore", e));
        }
    }

    public void deleteFromWatchlist(int movieId){
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("watchlist")
                    .document(String.valueOf(movieId))
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Film usuniety z listy do obejrzenia!"))
                    .addOnFailureListener(e -> Log.e("FIRESTORE", "Blad usuwania filmu z Firestore", e));
        }
    }

    public void addToWatched(Movie movie) {
        if (currentUser != null) {
            Map<String, Object> movieData = getMovieData(movie);
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("watched")
                    .document(String.valueOf(movie.getId()))
                    .set(movieData)
                    .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Film dodany do obejrzanych!"))
                    .addOnFailureListener(e -> Log.e("FIRESTORE", "Błąd zapisu do Firestore", e));
        }
    }

    public void deleteFromWatched(int movieId) {
        if (currentUser != null) {
            db.collection("users")
                    .document(currentUser.getUid())
                    .collection("watched")
                    .document(String.valueOf(movieId))
                    .delete()
                    .addOnSuccessListener(aVoid -> Log.d("FIRESTORE", "Film usuniety z listy obejrzanych!"))
                    .addOnFailureListener(e -> Log.e("FIRESTORE", "Blad usuwania filmu z Firestore", e));
        }
    }

    public interface OnMoviesLoadedListener { //zapytanie asynchroniczne, przekazujemy dane dopiero po pobraniu
        void onMoviesLoaded(List<Movie> movies);
    }


    public void getWatched(OnMoviesLoadedListener listener) {
        List<Movie> movies = new ArrayList<>();
        db.collection("users")
                .document(currentUser.getUid())
                .collection("watched")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (DocumentSnapshot document : querySnapshot) { //iterowanie po wszystkich dokumentach
                                    Movie movie = document.toObject(Movie.class);
                                    movies.add(movie);
                                }
                                Log.d("FIRESTORE", "OBEJRZANE: " + movies.size());
                                watchedMovies = movies;
                                listener.onMoviesLoaded(movies);
                            }
                        } else {
                            Log.e("FIRESTORE", "Error getting watched movies", task.getException());
                        }
                    }
                });
    }

    public void getWatchlist(OnMoviesLoadedListener listener) {
        List<Movie> movies = new ArrayList<>();
        db.collection("users")
                .document(currentUser.getUid())
                .collection("watchlist")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null) {
                                for (DocumentSnapshot document : querySnapshot) {
                                    Movie movie = document.toObject(Movie.class);
                                    movies.add(movie);
                                }
//                                Log.d("FIRESTORE", "DO OBEJRZENIA: " + movies.size());
                                toWatchMovies = movies;
                                listener.onMoviesLoaded(movies);
                            }
                        } else {
                            Log.e("FIRESTORE", "Error getting movies to watch", task.getException());
                        }
                    }
                });
    }
}

