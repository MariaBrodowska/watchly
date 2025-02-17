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
    public void flipCard(View convertView, View frontView, View backView) {
//        if (frontView.getVisibility() == View.VISIBLE) {
//            // Flip to back
//            frontView.setVisibility(View.INVISIBLE);
//            backView.setVisibility(View.VISIBLE);
//        } else {
//            // Flip to front
//            backView.setVisibility(View.INVISIBLE);
//            frontView.setVisibility(View.VISIBLE);
//        }
        convertView.animate().rotationY(80f).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                convertView.setRotationY(-100f);
                frontView.setVisibility(View.INVISIBLE);
                backView.setVisibility(View.VISIBLE);
                convertView.animate().rotationY(0f).setDuration(1000).start();
            }
        }).start();
    }

    private void setStars(double rating, ImageView[] stars) {
        int fullStars = (int) (Math.round(rating) / 2);
        boolean hasHalfStar = (Math.round(rating) % 2) >= 1;

        for (int i = 0; i < stars.length; i++) {
            if (i < fullStars) {
                stars[i].setImageResource(R.drawable.star_full);
            } else if (i == fullStars && hasHalfStar) {
                stars[i].setImageResource(R.drawable.star_half);
            } else {
                stars[i].setImageResource(R.drawable.star_empty);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_movie_card, parent, false);
        }
        ImageView poster = convertView.findViewById(R.id.moviePoster);
        View frontView = convertView.findViewById(R.id.frontLayout);
        View backView = convertView.findViewById(R.id.backLayout);
        Movie movie = getItem(position);

        ImageView secondPoster = backView.findViewById(R.id.secondPoster);
        TextView title = backView.findViewById(R.id.movieTitle);
        TextView review = backView.findViewById(R.id.review);
        TextView reviewCount = backView.findViewById(R.id.reviewCount);
        TextView description = backView.findViewById(R.id.description);

        title.setText(movie.getTitle());
        review.setText(String.format("%.2f", movie.getVote_average()));
        reviewCount.setText("("+String.valueOf(movie.getVote_count())+")");
        ImageView[] stars = new ImageView[]{
                backView.findViewById(R.id.star1),
                backView.findViewById(R.id.star2),
                backView.findViewById(R.id.star3),
                backView.findViewById(R.id.star4),
                backView.findViewById(R.id.star5)
        };
        setStars(movie.getVote_average(), stars);

        description.setText(movie.getOverview());

        String posterPath = movie.getPoster_path();
        String secondPath = movie.getBackdrop_path();

        if (posterPath != null && !posterPath.isEmpty()) {
            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + posterPath)
                    .into(poster);
            Log.d("MovieAdapter", "0 err");
        } else {
            Log.d("MovieAdapter", "Poster Path err: " + posterPath);

        }
        if (secondPath != null && !secondPath.isEmpty()) {
            Glide.with(context)
                    .load("https://image.tmdb.org/t/p/w500" + secondPath)
                    .into(secondPoster);
        }

        if (movie.isFlipped()) {
            Log.d("MovieAdapter", "Dziala 1!");
            frontView.setVisibility(View.INVISIBLE);
            backView.setVisibility(View.VISIBLE);
        } else {
            Log.d("MovieAdapter", "Dziala 2!");
            frontView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.INVISIBLE);
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
