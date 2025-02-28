package com.example.watchly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Pages pages = new Pages(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        RadioButton[] radioButtons = {findViewById(R.id.allTypesButton), findViewById(R.id.movieButton), findViewById(R.id.seriesButton)};
        for(RadioButton button : radioButtons){
            button.setOnCheckedChangeListener(((buttonView, isChecked) -> {
                if (isChecked){
                    for(RadioButton other : radioButtons){
                        if(other != button){
                            other.setChecked(false);
                        }
                    }
                }
            }));
        }

        RecyclerView genreRecyclerView = findViewById(R.id.genreRecyclerView);

        List<String> genres = Arrays.asList("Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Fantasy");

        genreRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        GenreAdapter adapter = new GenreAdapter(this, genres);
        genreRecyclerView.setAdapter(adapter);
    }
}
