package com.example.watchly;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder> {
    private Context context;
    public List<Movie> movies;
    private String pageType;
    private FirestoreManager fm = new FirestoreManager();

    public MovieListAdapter(Context context, List<Movie> movies, String pageType) {
        this.context = context;
        this.movies = movies;
        this.pageType = pageType;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie_list, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);

        holder.movieTitle.setText(movie.getTitleName());
        Glide.with(context)
                .load("https://image.tmdb.org/t/p/w500" + movie.getPoster_path())
                .into(holder.moviePoster);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("movie", movie);  //przeslanie obiektu
            intent.putExtra("pageType", pageType);
            context.startActivity(intent);
        });

        if(pageType.equals("seen")) {
            holder.delete.setText("Delete from Seen");
            holder.add.setText("Add to Watchlist");
            holder.delete.setOnClickListener(v -> {
                Toast.makeText(context, movie.getTitleName() + " deleted from seen!", Toast.LENGTH_SHORT).show();
                fm.deleteFromWatched(movie.getId());
                updateAfterDelete(position);
            });
            holder.add.setOnClickListener(v -> {
                Toast.makeText(context, movie.getTitleName() + " added to watchlist!", Toast.LENGTH_SHORT).show();
                fm.addToWatchlist(movie);
                fm.deleteFromWatched(movie.getId());
                updateAfterDelete(position);
            });
        }
        else if(pageType.equals("watchlist")){
            holder.delete.setText("Delete from Watchlist");
            holder.add.setText("Add to Seen");
            holder.delete.setOnClickListener(v -> {
                Toast.makeText(context, movie.getTitleName() + " deleted from watchlist!", Toast.LENGTH_SHORT).show();
                fm.deleteFromWatchlist(movie.getId());
                updateAfterDelete(position);
            });
            holder.add.setOnClickListener(v -> {
                Toast.makeText(context, movie.getTitleName() + " added to seen!", Toast.LENGTH_SHORT).show();
                fm.addToWatched(movie);
                fm.deleteFromWatchlist(movie.getId());
                updateAfterDelete(position);
            });
        }
        else if(pageType.equals("search")){
            holder.delete.setBackground(ContextCompat.getDrawable(context, R.drawable.search_state));
            holder.add.setBackground(ContextCompat.getDrawable(context, R.drawable.search_state));
            holder.delete.setText("Add to Seen");
            holder.add.setText("Add to Watchlist");
            holder.add.setOnClickListener(v -> {
                Toast.makeText(context, movie.getTitleName() + " added to watchlist!", Toast.LENGTH_SHORT).show();
                fm.addToWatchlist(movie);
                fm.deleteFromWatched(movie.getId());
                updateAfterDelete(position);
            });
            holder.delete.setOnClickListener(v -> {
                Toast.makeText(context, movie.getTitleName() + " added to seen!", Toast.LENGTH_SHORT).show();
                fm.addToWatched(movie);
                fm.deleteFromWatchlist(movie.getId());
                updateAfterDelete(position);
            });
        }
    }
    public void updateAfterDelete(int position){
        movies.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, movies.size());
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle;
        ImageView moviePoster;
        TextView delete;
        TextView add;

        public MovieViewHolder(View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            moviePoster = itemView.findViewById(R.id.moviePoster);
            delete = itemView.findViewById(R.id.delete);
            add = itemView.findViewById(R.id.add);
        }
    }
}
