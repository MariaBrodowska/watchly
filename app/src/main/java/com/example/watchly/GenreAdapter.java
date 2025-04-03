package com.example.watchly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GenreAdapter extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {
    private final List<String> genres;
    private final Context context;
    private List<String> selectedGenres = new ArrayList<>();


    public GenreAdapter(Context context, List<String> genres) {
        this.context = context;
        this.genres = genres;
    }

    public List<String> getSelectedGenres() {
        return selectedGenres;
    }

    public void clear() {
        selectedGenres.clear();
        notifyDataSetChanged();
    }

    @Override
    public GenreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre_checkbox, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenreViewHolder holder, int position) {
        String genre = genres.get(position);
        holder.checkBox.setText(genre);
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGenres.add(genre);
            } else {
                selectedGenres.remove(genre);
            }
        });
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    public static class GenreViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public GenreViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.genreCheckBox);
        }
    }
}
