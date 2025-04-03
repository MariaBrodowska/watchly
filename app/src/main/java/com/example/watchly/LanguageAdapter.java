package com.example.watchly;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder> {
    private final List<String> languages;
    private final Context context;

    private List<String> selectedLanguages = new ArrayList<>();

    public LanguageAdapter(Context context, List<String> languages) {
        this.context = context;
        this.languages = languages;
    }

    public List<String> getSelectedLanguages(){
        return selectedLanguages;
    }

    @Override
    public LanguageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_genre_checkbox, parent, false);
        return new LanguageViewHolder(view);
    }

    public void clear() {
        selectedLanguages.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(LanguageViewHolder holder, int position) {
        String language = languages.get(position);
        holder.checkBox.setText(language);
        holder.checkBox.setChecked(selectedLanguages.contains(language));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                selectedLanguages.add(language);
                Log.d("BLAD", "GFGS");
            }
            else{
                selectedLanguages.remove(language);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public static class LanguageViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public LanguageViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.genreCheckBox);
        }
    }
}
