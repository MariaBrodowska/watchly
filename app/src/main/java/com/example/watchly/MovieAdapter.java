package com.example.watchly;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import android.widget.BaseAdapter;

public class MovieAdapter extends BaseAdapter {
    private Context context;
    private List<Movie> movies;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public int getCount() {
        return movies.size();
    }

    @Override
    public Movie getItem(int position) {
        return movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_movie_card, parent, false);
        }

//        TextView title = convertView.findViewById(R.id.movieTitle);
        ImageView poster = convertView.findViewById(R.id.moviePoster);

        Movie movie = getItem(position);
//        title.setText(movie.getTitle());

        String posterPath = movie.getPoster_path();

        if (posterPath != null && !posterPath.isEmpty()) {
            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + posterPath)
                    .into(poster);
        } else {
            Log.d("MovieAdapter", "Poster Path err: " + posterPath);

        }

        return convertView;
    }


    public void addAll(List<Movie> newMovies) {
        movies.addAll(newMovies);
        notifyDataSetChanged();
    }

    public void removeFirst() {
        if (!movies.isEmpty()) {
            movies.remove(0);
            notifyDataSetChanged();
        }
    }
}
